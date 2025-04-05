package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.blocks.machine.ReactorControllerBlock
import io.enderdev.alchemistry.client.BlockHighlighter
import io.enderdev.alchemistry.recipes.IRecipe
import io.enderdev.alchemistry.recipes.register.AbstractRecipeRegister
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.IFluidBlock
import kotlin.math.roundToInt

abstract class AbstractReactorController<T : IRecipe>(val reactorType: ReactorType, recipeRegister: AbstractRecipeRegister<T>) : AbstractMachine<T>(recipeRegister), IEnergyTile {
	val shapeHandler = ReactorShapeHandler(this)
	val fluidModifiers = mutableMapOf<Fluid, Multiplier>()
	val moderatorModifiers = mutableMapOf<BlockMeta, Multiplier>()
	var currentMultiplier = Multiplier()
	var isMultiblockValid = false
	var checkMultiblockTicks = 0

	fun getFacing() = world?.getBlockState(pos)?.getValue(ReactorControllerBlock.Companion.FACING)

	fun updateMultiblock() {
		val highlight = !isMultiblockValid && world?.isRemote == true && shapeHandler.failPos != null && BlockHighlighter.pos == shapeHandler.failPos

		isMultiblockValid = validateMultiblock()

		if(!isMultiblockValid && highlight && BlockHighlighter.pos != shapeHandler.failPos)
			shapeHandler.highlightIncorrect()
	}

	fun validateMultiblock() = shapeHandler.validate()

	fun updateModifiers() {
		val (fluids, blocks) = shapeHandler.countInside()
		currentMultiplier.reset()
		fluids.map { (fluid: Fluid, cnt: Int) ->
			fluidModifiers[fluid]?.let { (productivity, processingTime, energy) ->
				currentMultiplier.productivity += productivity * cnt
				currentMultiplier.processingTime += processingTime * cnt
				currentMultiplier.energy += energy * cnt
			}
		}
		blocks.map { (state: IBlockState, cnt: Int) ->
			moderatorModifiers.entries.forEach { (wanted, mod) ->
				if(wanted.matches(state)) {
					currentMultiplier.productivity += mod.productivity * cnt
					currentMultiplier.processingTime += mod.processingTime * cnt
					currentMultiplier.energy += mod.energy * cnt
				}
			}
		}
	}

	fun loadConfig(fluidConfig: Array<String>, moderatorConfig: Array<String>) {
		fluidModifiers.clear()
		fluidConfig.forEach {
			/**
			 * We only added commas here, because we spent way too long debugging it once
			 * _silently cries in the corner_
			 *       ,_     _,
			 *      |\\___//|
			 *      |=6   6=|
			 *      \=._Y_.=/
			 *       )  `  (    ,
			 *      /       \  ((
			 *      |       |   ))
			 *     /| |   | |\_//
			 *     \| |._.| |/-`
			 *      '"'   '"'
			 */
			val split = it.split(";", ",")
			if(split.size != 4) {
				Alchemistry.logger.error("Malformed ${reactorType.name.lowercase()} fluid modifier config entry - expected 4 sections but found ${split.size}: $it")
				return@forEach
			}
			val fluid = FluidRegistry.getFluid(split[0])
			if(fluid == null) {
				Alchemistry.logger.error("Malformed ${reactorType.name.lowercase()} fluid modifier config entry - fluid not found: ${split[0]}")
				return@forEach
			}
			fluidModifiers[fluid] = Multiplier(split[1].toDouble(), split[2].toDouble(), split[3].toDouble())
		}
		moderatorModifiers.clear()
		moderatorConfig.forEach {
			// mod:block[:meta];productivity;processing_time;energy
			val split = it.split(';', ',')
			if(split.size != 4) {
				Alchemistry.logger.error("Malformed ${reactorType.name.lowercase()} moderator block modifier config entry - expected 4 sections but found ${split.size}: $it")
				return@forEach
			}
			val blockSplit = split[0].split(':')
			if(blockSplit.size != 2 && blockSplit.size != 3) {
				Alchemistry.logger.error("Malformed ${reactorType.name.lowercase()} moderator block modifier config entry - invalid block specification: ${split[0]}")
				return@forEach
			}
			val block = Block.REGISTRY.getObject(ResourceLocation(blockSplit[0], blockSplit[1]))
			if(block == Blocks.AIR || block is IFluidBlock) {
				Alchemistry.logger.error("Malformed ${reactorType.name.lowercase()} moderator block modifier config entry - invalid block (doesn't exist or is a fluid): ${split[0]}")
				return@forEach
			}
			moderatorModifiers[BlockMeta(block, blockSplit.getOrNull(2)?.toInt() ?: 0)] =
				Multiplier(split[1].toDouble(), split[2].toDouble(), split[3].toDouble())
		}
	}

	fun getModifiedProcessTime(default: Int) = (default * currentMultiplier.processingTime).roundToInt().coerceAtLeast(0)

	fun getModifiedEnergyCost(default: Int) = (default * currentMultiplier.energy).roundToInt().coerceAtLeast(
		if(reactorType == ReactorType.FISSION) ConfigHandler.FISSION.minEnergyPerTick else ConfigHandler.FUSION.minEnergyPerTick
	)

	override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
		if(isMultiblockValid) super.hasCapability(capability, facing) else false

	override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?) =
		if(isMultiblockValid) super.getCapability(capability, facing) else null

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(compound)
		compound.setInteger("ProgressTicks", progressTicks)
		compound.setDouble("productivityMult", currentMultiplier.productivity)
		compound.setDouble("processingTimeMult", currentMultiplier.processingTime)
		compound.setDouble("energyMult", currentMultiplier.energy)
		return compound
	}

	override fun readFromNBT(compound: NBTTagCompound) {
		super.readFromNBT(compound)
		progressTicks = compound.getInteger("ProgressTicks")
		currentMultiplier = Multiplier(
			compound.getDouble("productivityMult"),
			compound.getDouble("processingTimeMult"),
			compound.getDouble("energyMult")
		)
		updateMultiblock()
	}

	data class Multiplier(var productivity: Double = 1.0, var processingTime: Double = 1.0, var energy: Double = 1.0) {
		fun reset() {
			productivity = 1.0
			processingTime = 1.0
			energy = 1.0
		}
	}

	data class BlockMeta(val block: Block, val meta: Int) {
		fun matches(state: IBlockState) = state.block == block && block.getMetaFromState(state) == meta
	}
}
