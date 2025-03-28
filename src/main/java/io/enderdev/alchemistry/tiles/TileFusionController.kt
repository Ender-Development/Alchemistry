package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.blocks.ModBlocks
import io.enderdev.alchemistry.blocks.PropertyPowerStatus
import io.enderdev.alchemistry.blocks.machine.FusionControllerBlock
import io.enderdev.alchemistry.chemistry.ChemicalElement
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.FusionRecipe
import io.enderdev.alchemistry.recipes.register.FusionRegister
import io.enderdev.alchemistry.tiles.tags.EnergyTileImpl
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import io.enderdev.alchemistry.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by al132 on 4/29/2017.
 */
class TileFusionController : AbstractReactorController<FusionRecipe>(ReactorType.FUSION, FusionRegister.Companion.INSTANCE),
    IEnergyTile by EnergyTileImpl(ConfigHandler.FUSION.energyCapacity) {

    var recipeOutput: ItemStack = ItemStack.EMPTY
    var singleMode: Boolean = false

    override val energyPerTick: Int
        get() = getModifiedEnergyCost(ConfigHandler.FUSION.energyPerTick)

    override val recipeTime: Int
        get() = getModifiedProcessTime(ConfigHandler.FUSION.processingTicks)

    init {
        initInventoryCapability(2, 1)
        loadConfig(ConfigHandler.FUSION.fusionReactorModifiers)
    }

    override fun initInventoryInputCapability() {
        input = object : TileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (singleMode) {
                    return if (this.getStackInSlot(slot).isEmpty) super.insertItem(slot, stack, simulate)
                    else stack
                }
                return if (stack.item == ModItems.elements)
                    super.insertItem(slot, stack, simulate)
                else stack
            }
        }
    }

    override fun updateRecipe() {
        val meta1 = this.input[0].metadata
        val meta2 = this.input[1].metadata
        recipeRegister.firstOrNull { it.inputMeta1 == meta1 && it.inputMeta2 == meta2 }?.let { currentRecipe = it }
        val outputElement: ChemicalElement? = ElementRegistry[meta1 + meta2]
        if (outputElement != null) recipeOutput = outputElement.toItemStack(1)
        else recipeOutput = ItemStack.EMPTY
    }

    override fun onProcessComplete() {
        var stacksize = recipeOutput.count
        val staticMultiplier = productivityModifier.toInt()
        val randomMultiplier = if (productivityModifier - staticMultiplier > Math.random()) 1 else 0
        if (staticMultiplier != 0 || randomMultiplier != 0) {
            stacksize *= staticMultiplier + randomMultiplier
        }
        val outputStack = recipeOutput.copy()
        outputStack.count = stacksize
        output.setOrIncrement(0, outputStack)

        input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        input.decrementSlot(1, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
    }

    override fun onWorkTick() {
        this.energyStorage.extractEnergy(energyPerTick, false)
    }

    override fun shouldTick() = true

    override fun shouldProcess() =
        this.isMultiblockValid
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
        val isActive =
            !this.input[0].isEmpty && !this.input[1].isEmpty && energyStorage.energyStored >= energyPerTick
        val state = this.world.getBlockState(this.pos)
        if (state.block != ModBlocks.fusionController) return;
        val currentStatus = state.getValue(FusionControllerBlock.Companion.STATUS)
        if (this.isMultiblockValid) {
            if (isActive) {
                if (currentStatus != PropertyPowerStatus.ON) this.world.setBlockState(
                    this.pos,
                    state.withProperty(FusionControllerBlock.Companion.STATUS, PropertyPowerStatus.ON)
                )
            } else if (currentStatus != PropertyPowerStatus.STANDBY) world.setBlockState(
                pos,
                state.withProperty(FusionControllerBlock.Companion.STATUS, PropertyPowerStatus.STANDBY)
            )
            updateModifiers()
        } else if (currentStatus != PropertyPowerStatus.OFF) world.setBlockState(
            pos,
            state.withProperty(FusionControllerBlock.Companion.STATUS, PropertyPowerStatus.OFF)
        )
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setBoolean("singleMode", singleMode)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        this.singleMode = compound.getBoolean("singleMode")
        super.readFromNBT(compound)
    }
}