package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.blocks.ModBlocks
import io.enderdev.alchemistry.blocks.PropertyPowerStatus
import io.enderdev.alchemistry.blocks.machine.ReactorControllerBlock
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.FissionRecipe
import io.enderdev.alchemistry.recipes.register.FissionRegister
import io.enderdev.alchemistry.tiles.tags.EnergyTileImpl
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import io.enderdev.alchemistry.utils.extensions.get
import io.enderdev.alchemistry.utils.extensions.toStack
import net.minecraft.item.ItemStack
import kotlin.math.floor

/**
 * Created by al132 on 4/29/2017.
 */
class TileFissionController : AbstractReactorController<FissionRecipe>(ReactorType.FISSION, FissionRegister.Companion.INSTANCE),
    IEnergyTile by EnergyTileImpl(ConfigHandler.FISSION.energyCapacity) {
    override val guiHeight: Int
        get() = 222

    var recipeOutput1: ItemStack = ItemStack.EMPTY
    var recipeOutput2: ItemStack = ItemStack.EMPTY

    override val energyPerTick: Int
        get() = getModifiedEnergyCost(ConfigHandler.FISSION.energyPerTick)

    override val recipeTime: Int
        get() = getModifiedProcessTime(ConfigHandler.FISSION.processingTicks)

    init {
        initInventoryCapability(1, 2)
        loadConfig(ConfigHandler.FISSION.fissionReactorModifiers)
    }

    override fun initInventoryInputCapability() {
        input = object : TileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (stack.item == ModItems.elements && stack.metadata > 1)
                    super.insertItem(slot, stack, simulate)
                else stack
            }
        }
    }

    override fun updateRecipe() {
        val meta = this.input[0].metadata
        recipeRegister.firstOrNull { it.inputMeta == meta }?.let { currentRecipe = it }
        if (meta != 0) {
            if (meta % 2 == 0) {
                if (ElementRegistry[meta / 2] != null) {
                    recipeOutput1 = ModItems.elements.toStack(quantity = 2, meta = meta / 2)
                    recipeOutput2 = ItemStack.EMPTY
                    return
                }
            } else {
                if (ElementRegistry[meta / 2] != null && ElementRegistry[(meta / 2) + 1] != null) {
                    recipeOutput1 = ModItems.elements.toStack(meta = (meta / 2) + 1)
                    recipeOutput2 = ModItems.elements.toStack(meta = meta / 2)
                    return
                }
            }
        }
        recipeOutput1 = ItemStack.EMPTY
        recipeOutput2 = ItemStack.EMPTY
    }

    override fun onProcessComplete() {
        var stacksize1 = recipeOutput1.count
        val staticMultiplier = floor(productivityModifier).toInt()
        val randomMultiplier = if (productivityModifier - staticMultiplier > Math.random()) 1 else 0
        if (staticMultiplier != 0 || randomMultiplier != 0) {
            stacksize1 *= staticMultiplier + randomMultiplier
        }
        val outputStack1 = recipeOutput1.copy()
        outputStack1.count = if (stacksize1 > outputStack1.maxStackSize) outputStack1.maxStackSize else stacksize1
        output.setOrIncrement(0, outputStack1)
        if (!recipeOutput2.isEmpty) {
            var stacksize2 = recipeOutput2.count
            if (staticMultiplier != 0 || randomMultiplier != 0) {
                stacksize2 *= staticMultiplier + randomMultiplier
            }
            val outputStack2 = recipeOutput2.copy()
            outputStack2.count =
                if (stacksize2 > outputStack2.maxStackSize) outputStack2.maxStackSize else stacksize2
            output.setOrIncrement(1, outputStack2)
        }
        input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
    }

    override fun onWorkTick() {
        this.energyStorage.extractEnergy(energyPerTick, false)
    }

    override fun shouldTick(): Boolean = true

    override fun shouldProcess() =
        this.isMultiblockValid
                && !recipeOutput1.isEmpty
                && (ItemStack.areItemsEqual(output[0], recipeOutput1) || output[0].isEmpty)
                && (ItemStack.areItemsEqual(output[1], recipeOutput2) || output[1].isEmpty)
                && output[0].count + recipeOutput1.count <= recipeOutput1.maxStackSize
                && output[1].count + recipeOutput2.count <= recipeOutput2.maxStackSize
                && energyStorage.energyStored >= energyPerTick

    override fun onIdleTick() {
        super.onIdleTick()

        val isActive = !this.input[0].isEmpty && energyStorage.energyStored >= energyPerTick
        if (++checkMultiblockTicks == 20) {
            updateMultiblock()
            checkMultiblockTicks = 0
        }
        val state = this.world.getBlockState(this.pos)
        if (state.block != ModBlocks.fissionController) return;
        val currentStatus = state.getValue(ReactorControllerBlock.Companion.STATUS)
        if (this.isMultiblockValid) {
            if (isActive) {
                if (currentStatus != PropertyPowerStatus.ON) this.world.setBlockState(this.pos, state.withProperty(
                    ReactorControllerBlock.Companion.STATUS,
                    PropertyPowerStatus.ON
                ))
            } else if (currentStatus != PropertyPowerStatus.STANDBY) world.setBlockState(pos, state.withProperty(
                ReactorControllerBlock.Companion.STATUS,
                PropertyPowerStatus.STANDBY
            ))
            updateModifiers()
        } else if (currentStatus != PropertyPowerStatus.OFF) world.setBlockState(pos, state.withProperty(
            ReactorControllerBlock.Companion.STATUS,
            PropertyPowerStatus.OFF
        ))
    }
}