package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.blocks.machine.ReactorControllerBlock
import io.enderdev.alchemistry.client.BlockHighlighter
import io.enderdev.alchemistry.recipes.IRecipe
import io.enderdev.alchemistry.recipes.register.AbstractRecipeRegister
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import kotlin.math.roundToInt

abstract class AbstractReactorController<T : IRecipe>(val reactorType: ReactorType, recipeRegister: AbstractRecipeRegister<T>) : AbstractMachine<T>(recipeRegister), IEnergyTile {
	val shapeHandler = ReactorShapeHandler(this)
	var fluidModifiers = mutableMapOf<Fluid, Multiplier>()
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
		val fluids = shapeHandler.countFluid()
		currentMultiplier.reset()
		fluids.map { (fluid: Fluid, cnt: Int) ->
			fluidModifiers[fluid]?.let { (productivity, processingTime, energy) ->
				currentMultiplier.productivity += productivity * cnt
				currentMultiplier.processingTime += processingTime * cnt
				currentMultiplier.energy += energy * cnt
			}
		}
	}

	fun loadConfig(configValue: Array<String>) {
		fluidModifiers.clear()
		configValue.forEach {
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
	}

	fun getModifiedProcessTime(default: Int) = (default * currentMultiplier.processingTime).roundToInt()

	fun getModifiedEnergyCost(default: Int) = (default * currentMultiplier.energy).roundToInt()

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
}
