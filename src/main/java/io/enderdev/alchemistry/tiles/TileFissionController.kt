package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.blocks.ModBlocks
import io.enderdev.alchemistry.blocks.PropertyPowerStatus
import io.enderdev.alchemistry.blocks.machine.ReactorControllerBlock
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.FissionRecipe
import io.enderdev.alchemistry.recipes.register.FissionRegister
import net.minecraft.item.ItemStack
import org.ender_development.catalyx.tiles.helper.EnergyTileImpl
import org.ender_development.catalyx.tiles.helper.IEnergyTile
import org.ender_development.catalyx.tiles.helper.TileStackHandler
import org.ender_development.catalyx.utils.extensions.get
import org.ender_development.catalyx.utils.extensions.toStack

class TileFissionController : AbstractReactorController<FissionRecipe>(ReactorType.FISSION, FissionRegister.Companion.INSTANCE),
	IEnergyTile by EnergyTileImpl(ConfigHandler.FISSION.energyCapacity) {
	override val guiHeight = 222

	var recipeOutput1: ItemStack = ItemStack.EMPTY
	var recipeOutput2: ItemStack = ItemStack.EMPTY

	override val energyPerTick: Int
		get() = getModifiedEnergyCost(ConfigHandler.FISSION.energyPerTick)

	override val recipeTime: Int
		get() = getModifiedProcessTime(ConfigHandler.FISSION.processingTicks)

	init {
		initInventoryCapability(1, 2)
		loadConfig(ConfigHandler.FISSION.moderators)
	}

	override fun initInventoryInputCapability() {
		input = object : TileStackHandler(inputSlots, this) {
			override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) =
				if(stack.item == ModItems.elements && stack.metadata > 1) super.insertItem(slot, stack, simulate)
				else stack
		}
	}

	override fun updateRecipe() {
		val meta = input[0].metadata
		recipeRegister.recipes.firstOrNull { it.inputMeta == meta }?.let { currentRecipe = it }
		recipeOutput1 = ItemStack.EMPTY
		recipeOutput2 = ItemStack.EMPTY
		if(meta == 0)
			return

		val half = meta ushr 1
		if(ElementRegistry[half] == null)
			return

		if(meta and 1 == 0)
			recipeOutput1 = ModItems.elements.toStack(2, half)
		else if(ElementRegistry[half + 1] != null) {
			recipeOutput1 = ModItems.elements.toStack(meta = half + 1)
			recipeOutput2 = ModItems.elements.toStack(meta = half)
		}
	}

	override fun onProcessComplete() {
		val (productivity) = currentMultiplier
		if(productivity > 0) {
			val stackMultiplier = productivity.toInt() + if(productivity - productivity.toInt() > world.rand.nextDouble()) 1 else 0

			recipeOutput1.copy().apply {
				count = (count * stackMultiplier).coerceIn(0, maxStackSize)
				output.setOrIncrement(0, this)
			}

			if(!recipeOutput2.isEmpty)
				recipeOutput2.copy().apply {
					count = (count * stackMultiplier).coerceIn(0, maxStackSize)
					output.setOrIncrement(1, this)
				}
		}
		input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
	}

	override fun onWorkTick() {
		energyStorage.extractEnergy(energyPerTick, false)
	}

	override fun shouldTick() = true

	override fun shouldProcess() =
		isMultiblockValid
				&& !recipeOutput1.isEmpty
				&& (ItemStack.areItemsEqual(output[0], recipeOutput1) || output[0].isEmpty)
				&& (ItemStack.areItemsEqual(output[1], recipeOutput2) || output[1].isEmpty)
				&& output[0].count + recipeOutput1.count <= recipeOutput1.maxStackSize
				&& output[1].count + recipeOutput2.count <= recipeOutput2.maxStackSize
				&& energyStorage.energyStored >= energyPerTick

	override fun onIdleTick() {
		super.onIdleTick()

		val isActive = !input[0].isEmpty && energyStorage.energyStored >= energyPerTick
		if(++checkMultiblockTicks == 20) {
			updateMultiblock()
			checkMultiblockTicks = 0
		}
		val state = world.getBlockState(pos)
		if(state.block != ModBlocks.fissionController) return
		val currentStatus = state.getValue(ReactorControllerBlock.Companion.STATUS)
		if(isMultiblockValid) {
			if(isActive) {
				if(currentStatus != PropertyPowerStatus.ON) world.setBlockState(
					pos, state.withProperty(
						ReactorControllerBlock.Companion.STATUS,
						PropertyPowerStatus.ON
					)
				)
			} else if(currentStatus != PropertyPowerStatus.STANDBY) world.setBlockState(
				pos, state.withProperty(
					ReactorControllerBlock.Companion.STATUS,
					PropertyPowerStatus.STANDBY
				)
			)
			updateModifiers()
		} else if(currentStatus != PropertyPowerStatus.OFF) world.setBlockState(
			pos, state.withProperty(
				ReactorControllerBlock.Companion.STATUS,
				PropertyPowerStatus.OFF
			)
		)
	}
}
