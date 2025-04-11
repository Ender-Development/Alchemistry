package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.blocks.ModBlocks
import io.enderdev.alchemistry.blocks.PropertyPowerStatus
import io.enderdev.alchemistry.blocks.machine.ReactorControllerBlock
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.FusionRecipe
import io.enderdev.alchemistry.recipes.register.FusionRegister
import io.enderdev.alchemistry.tiles.tags.EnergyTileImpl
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import io.enderdev.alchemistry.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class TileFusionController : AbstractReactorController<FusionRecipe>(ReactorType.FUSION, FusionRegister.Companion.INSTANCE), IEnergyTile by EnergyTileImpl(ConfigHandler.FUSION.energyCapacity) {
	override val guiHeight: Int
		get() = 222

	var recipeOutput: ItemStack = ItemStack.EMPTY
	var singleMode = false

	override val energyPerTick: Int
		get() = getModifiedEnergyCost(ConfigHandler.FUSION.energyPerTick)

	override val recipeTime: Int
		get() = getModifiedProcessTime(ConfigHandler.FUSION.processingTicks)

	init {
		initInventoryCapability(2, 1)
		loadConfig(ConfigHandler.FUSION.fusionReactorFluidModifiers, ConfigHandler.FUSION.fusionReactorBlockModifiers)
	}

	override fun initInventoryInputCapability() {
		input = object : TileStackHandler(inputSlots, this) {
			override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = if(singleMode) {
				if(getStackInSlot(slot).isEmpty) super.insertItem(slot, stack, simulate)
				else stack
			} else if(stack.item == ModItems.elements) super.insertItem(slot, stack, simulate)
			else stack
		}
	}

	override fun updateRecipe() {
		val meta1 = input[0].metadata
		val meta2 = input[1].metadata
		recipeRegister.firstOrNull { it.inputMeta1 == meta1 && it.inputMeta2 == meta2 }?.let { currentRecipe = it }
		recipeOutput = ElementRegistry[meta1 + meta2]?.toItemStack(1) ?: ItemStack.EMPTY
	}

	override fun onProcessComplete() {
		val (productivity) = currentMultiplier
		if(productivity > 0) {
			val stackMultiplier = productivity.toInt() + if(productivity - productivity.toInt() > world.rand.nextDouble()) 1 else 0

			recipeOutput.copy().apply {
				count = (count * stackMultiplier).coerceIn(0, maxStackSize)
				output.setOrIncrement(0, this)
			}
		}
		input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
		input.decrementSlot(1, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
	}

	override fun onWorkTick() {
		energyStorage.extractEnergy(energyPerTick, false)
	}

	override fun shouldTick() = true

	override fun shouldProcess() = isMultiblockValid
			&& !input[0].isEmpty
			&& !input[1].isEmpty
			&& !recipeOutput.isEmpty
			&& (ItemStack.areItemsEqual(output[0], recipeOutput) || output[0].isEmpty)
			&& output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
			&& energyStorage.energyStored >= energyPerTick

	override fun onIdleTick() {
		super.onIdleTick()

		if(++checkMultiblockTicks == 20) {
			updateMultiblock()
			checkMultiblockTicks = 0
		}
		val isActive = !input[0].isEmpty && !input[1].isEmpty && energyStorage.energyStored >= energyPerTick
		val state = world.getBlockState(pos)
		if(state.block != ModBlocks.fusionController) return
		val currentStatus = state.getValue(ReactorControllerBlock.Companion.STATUS)
		if(isMultiblockValid) {
			if(isActive) {
				if(currentStatus != PropertyPowerStatus.ON) world.setBlockState(
					pos, state.withProperty(ReactorControllerBlock.Companion.STATUS, PropertyPowerStatus.ON)
				)
			} else if(currentStatus != PropertyPowerStatus.STANDBY) world.setBlockState(
				pos, state.withProperty(ReactorControllerBlock.Companion.STATUS, PropertyPowerStatus.STANDBY)
			)
			updateModifiers()
		} else if(currentStatus != PropertyPowerStatus.OFF) world.setBlockState(
			pos, state.withProperty(ReactorControllerBlock.Companion.STATUS, PropertyPowerStatus.OFF)
		)
	}

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(compound)
		compound.setBoolean("singleMode", singleMode)
		return compound
	}

	override fun readFromNBT(compound: NBTTagCompound) {
		singleMode = compound.getBoolean("singleMode")
		super.readFromNBT(compound)
	}
}
