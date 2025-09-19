package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.blocks.machine.ReactorControllerBlock
import io.enderdev.alchemistry.recipes.IRecipe
import io.enderdev.alchemistry.recipes.register.AbstractRecipeRegister
import io.enderdev.alchemistry.utils.BlockMeta
import io.enderdev.alchemistry.utils.ConfigUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import org.ender_development.catalyx.client.AreaHighlighter
import org.ender_development.catalyx.tiles.BaseMachineTile
import org.ender_development.catalyx.tiles.helper.IEnergyTile
import java.util.*
import kotlin.math.roundToInt

abstract class AbstractReactorController<T : IRecipe>(val reactorType: ReactorType, val recipeRegister: AbstractRecipeRegister<T>) : BaseMachineTile<T>(Alchemistry.catalyxSettings), IEnergyTile {
	val shapeHandler = ReactorShapeHandler(this)
	val moderators = mutableMapOf<BlockMeta, Multiplier>()
	var currentMultiplier = Multiplier()
	var isMultiblockValid = false
	var checkMultiblockTicks = 0
	val areaHighlighter = AreaHighlighter()

	fun getFacing() = world?.getBlockState(pos)?.getValue(ReactorControllerBlock.Companion.FACING)

	fun updateMultiblock() {
		val highlight = !isMultiblockValid && world?.isRemote == true && shapeHandler.failPos != null && areaHighlighter.pos1 == shapeHandler.failPos

		isMultiblockValid = validateMultiblock()

		if(!isMultiblockValid && highlight && areaHighlighter.pos1 != shapeHandler.failPos)
			shapeHandler.highlightIncorrect()
	}

	fun validateMultiblock() = shapeHandler.validate()

	fun updateModifiers() {
		val blocks = shapeHandler.countInside()
		currentMultiplier.reset()
		blocks.map { (state: IBlockState, cnt: Int) ->
			moderators.entries.forEach { (wanted, mod) ->
				if(wanted == state) {
					currentMultiplier.productivity += mod.productivity * cnt
					currentMultiplier.processingTime += mod.processingTime * cnt
					currentMultiplier.energy += mod.energy * cnt
				}
			}
		}
	}

	fun loadConfig(moderators: Array<String>) {
		this.moderators.clear()
		/*
		 * Commas `,` and semicolons `;` are annoyingly similar to eachother
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
		moderators.forEach {
			val tokenizer = StringTokenizer(it, ";")
			try {
				val block = ConfigUtils.parseBlock(tokenizer.nextToken()) ?: return@forEach
				this.moderators.put(block, Multiplier(tokenizer.nextToken().toDouble(), tokenizer.nextToken().toDouble(), tokenizer.nextToken().toDouble()))
			} catch(_: NoSuchElementException) {
				Alchemistry.logger.error("Invalid ${reactorType.name.lowercase(Locale.getDefault())} moderator entry, expected 4 columns separated by semicolons `;` but got: '$it'")
				if(it.contains(','))
					Alchemistry.logger.error("(You're most likely accidentally using commas `,` instead of semicolons `;`)")
			} catch(_: NumberFormatException) {
				Alchemistry.logger.error("Invalid ${reactorType.name.lowercase(Locale.getDefault())} moderator entry, expected 3 last columns to be numbers but got: '$it'")
			}
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
}
