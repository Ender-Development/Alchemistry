package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.LiquifierRecipe
import io.enderdev.alchemistry.recipes.register.LiquifierRegister
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate
import org.ender_development.catalyx.tiles.BaseMachineTile
import org.ender_development.catalyx.tiles.helper.EnergyTileImpl
import org.ender_development.catalyx.tiles.helper.IEnergyTile
import org.ender_development.catalyx.tiles.helper.IFluidTile
import org.ender_development.catalyx.tiles.helper.TileStackHandler
import org.ender_development.catalyx.utils.extensions.get

class TileLiquifier : BaseMachineTile<LiquifierRecipe>(Alchemistry.catalyxSettings), IFluidTile,
	IEnergyTile by EnergyTileImpl(ConfigHandler.LIQUIFIER.energyCapacity) {

	val outputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
		override fun canFillFluidType(fluid: FluidStack?) = recipeRegister.any { it.output.fluid == fluid?.fluid }

		override fun onContentsChanged() = markDirtyGUI()
	}.apply {
		setTileEntity(this@TileLiquifier)
		setCanFill(false)
		setCanDrain(true)
	}

	val recipeRegister = LiquifierRegister.Companion.INSTANCE.recipes

	override val energyPerTick = ConfigHandler.LIQUIFIER.energyPerTick
	override val recipeTime = ConfigHandler.LIQUIFIER.processingTicks

	override val fluidTanks = FluidHandlerConcatenate(outputTank)

	init {
		initInventoryCapability(1, 0)
	}

	override fun initInventoryInputCapability() {
		input = object : TileStackHandler(inputSlots, this) {
			override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) =
				if(recipeRegister.any { it.input.isItemEqual(stack) }) super.insertItem(slot, stack, simulate)
				else stack

			override fun onContentsChanged(slot: Int) = markDirtyGUI()
		}
	}

	override fun updateRecipe() {
		val inputStack = input.getStackInSlot(0)
		if(!inputStack.isEmpty
			&& (currentRecipe == null || !ItemStack.areItemStacksEqual(currentRecipe!!.input, inputStack))
		) {
			currentRecipe = recipeRegister.firstOrNull { ItemStack.areItemsEqual(it.input, inputStack) }
		}
		if(inputStack.isEmpty) currentRecipe = null
	}

	override fun onProcessComplete() {
		outputTank.fillInternal(currentRecipe!!.output.copy(), true)
		input[0].shrink(currentRecipe!!.input.count)
	}

	override fun onWorkTick() {
		energyStorage.extractEnergy(energyPerTick, false)
	}

	override fun shouldTick() = !input[0].isEmpty

	override fun shouldProcess(): Boolean {
		val recipeOutput = currentRecipe!!.output
		return (outputTank.capacity >= outputTank.fluidAmount + recipeOutput.amount
				&& energyStorage.energyStored >= energyPerTick
				&& input[0].count >= currentRecipe!!.input.count
				&& ((outputTank.fluid?.fluid == (recipeOutput.fluid ?: false)) || outputTank.fluid == null))
	}

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(compound)
		compound.setTag("OutputTankNBT", outputTank.writeToNBT(NBTTagCompound()))
		return compound
	}

	override fun readFromNBT(compound: NBTTagCompound) {
		super.readFromNBT(compound)
		outputTank.readFromNBT(compound.getCompoundTag("OutputTankNBT"))
	}
}
