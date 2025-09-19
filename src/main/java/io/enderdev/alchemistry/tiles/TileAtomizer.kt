package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.AtomizerRecipe
import io.enderdev.alchemistry.recipes.register.AtomizerRegister
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
import org.ender_development.catalyx.utils.extensions.get

class TileAtomizer : BaseMachineTile<AtomizerRecipe>(Alchemistry.catalyxSettings), IFluidTile,
	IEnergyTile by EnergyTileImpl(ConfigHandler.ATOMIZER.energyCapacity) {

	val inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
		override fun canFillFluidType(with: FluidStack?) = fluid == null || fluid!!.fluid == with?.fluid

		override fun onContentsChanged() = markDirtyGUI()
	}.apply {
		setTileEntity(this@TileAtomizer)
		setCanFill(true)
		setCanDrain(false)
	}

	override val energyPerTick = ConfigHandler.ATOMIZER.energyPerTick
	override val recipeTime = ConfigHandler.ATOMIZER.processingTicks

	override val fluidTanks = FluidHandlerConcatenate(inputTank)

	init {
		initInventoryCapability(0, 1)
	}

	override fun updateRecipe() {
		if(inputTank.fluid != null
			&& (currentRecipe == null || !ItemStack.areItemStacksEqual(currentRecipe!!.output, output.getStackInSlot(0)))
		) {
			currentRecipe = AtomizerRegister.INSTANCE.recipes.firstOrNull { it.input.fluid == inputTank.fluid?.fluid }
		}
		if(inputTank.fluid == null) currentRecipe = null
	}

	override fun onProcessComplete() {
		output.setOrIncrement(0, currentRecipe!!.output.copy())
		inputTank.drainInternal(currentRecipe!!.input.amount, true)
	}

	override fun onWorkTick() {
		energyStorage.extractEnergy(energyPerTick, false)
	}

	override fun shouldTick() = inputTank.fluidAmount > 0

	override fun shouldProcess(): Boolean {
		val recipeOutput = currentRecipe!!.output
		return energyStorage.energyStored >= energyPerTick
				&& inputTank.fluidAmount >= currentRecipe!!.input.amount
				&& (ItemStack.areItemsEqual(output[0], recipeOutput) || output[0].isEmpty)
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
}
