package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.EvaporatorRecipe
import io.enderdev.alchemistry.recipes.register.EvaporatorRegister
import io.enderdev.alchemistry.utils.BlockMeta
import io.enderdev.alchemistry.utils.ConfigUtils
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate
import org.ender_development.catalyx.tiles.BaseMachineTile
import org.ender_development.catalyx.tiles.helper.IFluidTile
import org.ender_development.catalyx.utils.extensions.get
import kotlin.math.roundToInt

class TileEvaporator : BaseMachineTile<EvaporatorRecipe>(Alchemistry.catalyxSettings), IFluidTile {

	val inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
		override fun canFillFluidType(fluid: FluidStack?) = recipeRegister.any { it.input.fluid == fluid?.fluid }

		override fun onContentsChanged() = markDirtyClient()
	}.apply {
		setTileEntity(this@TileEvaporator)
		setCanFill(true)
		setCanDrain(false)
	}

	val recipeRegister = EvaporatorRegister.Companion.INSTANCE.recipes

	override val energyPerTick = 0
	override val recipeTime: Int
		get() = calculateProcessingTime(ConfigHandler.EVAPORATOR.processingTicks)

	override val fluidTanks = FluidHandlerConcatenate(inputTank)

	init {
		initInventoryCapability(0, 1)
	}

	override fun updateRecipe() {
		val inputStack = inputTank.fluid
		if((inputStack != null) && (currentRecipe == null || currentRecipe!!.input.fluid == inputStack.fluid)) {
			currentRecipe = recipeRegister.firstOrNull { it.input.fluid == inputStack.fluid }
		}
		if(inputStack == null) currentRecipe = null
	}

	override fun onProcessComplete() {
		output.setOrIncrement(0, currentRecipe!!.output.copy())
		inputTank.drainInternal(currentRecipe!!.input.amount, true)
	}

	override fun onWorkTick() {}

	override fun shouldTick() = inputTank.fluidAmount > 0

	override fun shouldProcess(): Boolean {
		val recipeOutput = currentRecipe!!.output
		return inputTank.fluidAmount >= currentRecipe!!.input.amount
				&& (inputTank.fluid == null || inputTank.fluid!!.fluid == currentRecipe!!.input.fluid)
				&& (output[0].isEmpty || output[0].item == currentRecipe!!.output.item)
				&& output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
	}

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(compound)
		compound.setTag("InputTankNBT", inputTank.writeToNBT(NBTTagCompound()))
		return compound
	}

	override fun readFromNBT(compound: NBTTagCompound) {
		super.readFromNBT(compound)
		inputTank.readFromNBT(compound.getCompoundTag("InputTankNBT"))
	}

	fun calculateProcessingTime(config: Int) = (config / getHeat()).roundToInt()

	// TODO more elaborate calculation?
	fun getHeat(): Double {
		var heat = 1.0

		if(!BiomeDictionary.hasType(world.getBiomeForCoordsBody(pos), BiomeDictionary.Type.DRY))
			heat = 0.5

		val below = world.getBlockState(pos.down())
		heatSources.forEach { (block, speed) ->
			if(block == below)
				heat *= speed
		}
		return heat
	}

	companion object {
		@Suppress("UNCHECKED_CAST") // stfu IntelliJ
		val heatSources = ConfigHandler.EVAPORATOR.heatSources.map {
			val split = it.split(';', ',')
			if(split.size != 2) {
				Alchemistry.logger.error("Malformed evaporator heat source - expected 2 sections but found ${split.size}: $it")
				return@map null
			}
			val block = ConfigUtils.parseBlock(split[0]) ?: return@map null
			val multiplier = split[1].toDouble()
			block to multiplier
		}.filter { it != null }.toTypedArray() as Array<Pair<BlockMeta, Double>>
	}
}
