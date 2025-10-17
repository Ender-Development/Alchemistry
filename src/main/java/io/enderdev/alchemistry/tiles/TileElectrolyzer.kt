package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.ElectrolyzerRecipe
import io.enderdev.alchemistry.recipes.register.ElectrolyzerRegister
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate
import org.ender_development.catalyx.tiles.BaseMachineTile
import org.ender_development.catalyx.tiles.helper.EnergyTileImpl
import org.ender_development.catalyx.tiles.helper.IEnergyTile
import org.ender_development.catalyx.tiles.helper.IFluidTile
import org.ender_development.catalyx.tiles.helper.TileStackHandler
import org.ender_development.catalyx.utils.FluidTankUtils
import org.ender_development.catalyx.utils.extensions.containsItem
import org.ender_development.catalyx.utils.extensions.get
import org.ender_development.catalyx.utils.extensions.mapUnique

class TileElectrolyzer : BaseMachineTile<ElectrolyzerRecipe>(Alchemistry), IFluidTile, IEnergyTile by EnergyTileImpl(ConfigHandler.ELECTROLYZER.energyCapacity) {
	val recipeRegister = ElectrolyzerRegister.Companion.INSTANCE.recipes

	val inputTank = FluidTankUtils.create(this, Fluid.BUCKET_VOLUME * 10, true, false, fluidWhitelist = recipeRegister.mapUnique { it.input.fluid }.toTypedArray(), this::markDirtyGUI)

	override val energyPerTick = ConfigHandler.ELECTROLYZER.energyPerTick
	override val recipeTime = ConfigHandler.ELECTROLYZER.processingTicks

	override val fluidHandler = inputTank

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

	override fun shouldTick() =
		inputTank.fluidAmount > 0

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
