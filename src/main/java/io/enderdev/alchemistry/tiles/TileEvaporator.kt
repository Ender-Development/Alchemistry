package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.EvaporatorRecipe
import io.enderdev.alchemistry.recipes.register.EvaporatorRegister
import io.enderdev.alchemistry.tiles.tags.IFluidTile
import io.enderdev.alchemistry.utils.extensions.get
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 4/29/2017.
 */
class TileEvaporator : AbstractMachine<EvaporatorRecipe>(EvaporatorRegister.Companion.INSTANCE), IFluidTile {

	val inputTank: FluidTank

	override val energyPerTick: Int
		get() = 0

	override val recipeTime: Int
		get() = calculateProcessingTime(ConfigHandler.EVAPORATOR.processingTicks)

	override val fluidTanks: FluidHandlerConcatenate?
		get() = FluidHandlerConcatenate(inputTank)

	init {
		initInventoryCapability(0, 1)

		inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
			override fun canFillFluidType(fluid: FluidStack?) = recipeRegister.any { it.input.fluid == fluid?.fluid }

			override fun onContentsChanged() =
				markDirtyClient()
		}

		inputTank.setTileEntity(this)
		inputTank.setCanFill(true)
		inputTank.setCanDrain(false)
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

	// TODO more elaborate calculation?
	private fun calculateProcessingTime(config: Int): Int {
		var temp = config
		if(!BiomeDictionary.hasType(world.getBiomeForCoordsBody(pos), BiomeDictionary.Type.DRY)) {
			temp += (config * .5).toInt()
		}
		return temp
	}
}
