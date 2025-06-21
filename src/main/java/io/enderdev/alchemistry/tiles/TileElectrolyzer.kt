package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.ElectrolyzerRecipe
import io.enderdev.alchemistry.recipes.register.ElectrolyzerRegister
import io.enderdev.catalyx.utils.extensions.containsItem
import io.enderdev.catalyx.utils.extensions.get
import io.enderdev.catalyx.tiles.BaseMachineTile
import io.enderdev.catalyx.tiles.helper.EnergyTileImpl
import io.enderdev.catalyx.tiles.helper.IEnergyTile
import io.enderdev.catalyx.tiles.helper.IFluidTile
import io.enderdev.catalyx.tiles.helper.TileStackHandler
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

class TileElectrolyzer : BaseMachineTile<ElectrolyzerRecipe>(Alchemistry.catalyxSettings), IFluidTile,
	IEnergyTile by EnergyTileImpl(ConfigHandler.ELECTROLYZER.energyCapacity) {

	val inputTank: FluidTank

	val recipeRegister = ElectrolyzerRegister.Companion.INSTANCE.recipes

	override val fluidTanks: FluidHandlerConcatenate?
		get() = FluidHandlerConcatenate(inputTank)

	override val recipeTime: Int
		get() = ConfigHandler.ELECTROLYZER.processingTicks

	override val energyPerTick: Int
		get() = ConfigHandler.ELECTROLYZER.energyPerTick

	override fun updateRecipe() {
		val inputStack = inputTank.fluid
		if((inputStack != null) && (currentRecipe == null || currentRecipe!!.input.fluid == inputStack.fluid)) {
			currentRecipe = recipeRegister.firstOrNull { it.input.fluid == inputStack.fluid }
		}
		if(inputStack == null) currentRecipe = null
	}

	override fun onProcessComplete() {
		inputTank.drainInternal(currentRecipe!!.input.amount, true)

		if(world.rand.nextInt(100) < currentRecipe!!.electrolyteConsumptionChance) {
			input.decrementSlot(0, currentRecipe!!.electrolytes[0].count)
		}

		(0..3).forEach { output.setOrIncrement(it, currentRecipe!!.calculatedInSlot(it)) }
	}

	override fun onWorkTick() {
		energyStorage.extractEnergy(ConfigHandler.ELECTROLYZER.energyPerTick, false)
	}

	override fun shouldTick() = inputTank.fluidAmount > 0

	override fun shouldProcess() =
		inputTank.fluidAmount >= currentRecipe!!.input.amount
				&& input[0].count >= currentRecipe!!.electrolytes[0].count
				&& energyStorage.energyStored >= energyPerTick
				&& (0..3).all {
			val outputStack = output[it]
			val recipeStack = currentRecipe!!.outputs[it].copy()
			(outputStack.isEmpty || ItemStack.areItemsEqual(outputStack, recipeStack))
					&& outputStack.count + recipeStack.count <= recipeStack.maxStackSize
		}

	init {
		initInventoryCapability(1, 4)

		inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
			override fun canFillFluidType(fluid: FluidStack?) =
				recipeRegister.any { it.input.fluid == fluid?.fluid }

			override fun onContentsChanged() = markDirtyGUI()
		}

		inputTank.setTileEntity(this)
		inputTank.setCanFill(true)
		inputTank.setCanDrain(false)
	}

	override fun initInventoryInputCapability() {
		input = object : TileStackHandler(inputSlots, this) {
			override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
				return if(recipeRegister.any { it.electrolytes.containsItem(stack) })
					super.insertItem(slot, stack, simulate)
				else
					stack
			}
		}
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
