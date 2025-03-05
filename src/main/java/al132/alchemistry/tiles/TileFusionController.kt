package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.blocks.FusionControllerBlock.Companion.STATUS
import al132.alchemistry.blocks.ModBlocks
import al132.alchemistry.blocks.PropertyPowerStatus
import al132.alchemistry.chemistry.ChemicalElement
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ModItems
import al132.alib.tiles.*
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import kotlin.math.floor

/**
 * Created by al132 on 4/29/2017.
 */
class TileFusionController(reactorType: ReactorType = ReactorType.FUSION,
                           override val defaultEnergyPerTick: Int = ConfigHandler.FUSION.energyPerTick,
                           override val defaultEnergyCapacity: Int = ConfigHandler.FUSION.energyCapacity,
                           override val defaultProcessTime: Int = ConfigHandler.FUSION.processingTicks
) : AbstractReactorController(reactorType),
    IGuiTile, ITickable, IItemTile,
    IEnergyTile by EnergyTileImpl(capacity = defaultEnergyCapacity) {

    var recipeOutput: ItemStack = ItemStack.EMPTY
    var singleMode: Boolean = false

    init {
        initInventoryCapability(2, 1)
        loadConfig(ConfigHandler.FUSION.fusionReactorModifiers)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (singleMode) {
                    return if (this.getStackInSlot(slot).isEmpty) super.insertItem(slot, stack, simulate)
                    else stack
                }
                return if (stack.item == ModItems.elements) {
                    super.insertItem(slot, stack, simulate)
                } else stack
            }

            override fun onContentsChanged(slot: Int) {
                (tile as TileFusionController).refreshRecipe()
                super.onContentsChanged(slot)
            }
        }
    }

    override fun refreshRecipe() {
        val meta1 = this.input[0].metadata
        val meta2 = this.input[1].metadata
        val outputElement: ChemicalElement? = ElementRegistry[meta1 + meta2]
        if (outputElement != null) recipeOutput = outputElement.toItemStack(1)
        else recipeOutput = ItemStack.EMPTY
    }

    override fun update() {
        if (!world.isRemote) {
            checkMultiblockTicks++
            if (checkMultiblockTicks >= 20) {
                updateMultiblock()
                checkMultiblockTicks = 0
            }
            val isActive = !this.input[0].isEmpty && !this.input[1].isEmpty && energyStorage.energyStored >= getModifiedEnergyCost()
            val state = this.world.getBlockState(this.pos)
            if (state.block != ModBlocks.fusionController) return;
            val currentStatus = state.getValue(STATUS)
            if (this.isMultiblockValid) {
                if (isActive) {
                    if (currentStatus != PropertyPowerStatus.ON) this.world.setBlockState(
                        this.pos,
                        state.withProperty(STATUS, PropertyPowerStatus.ON)
                    )
                } else if (currentStatus != PropertyPowerStatus.STANDBY) world.setBlockState(
                    pos,
                    state.withProperty(STATUS, PropertyPowerStatus.STANDBY)
                )
                updateModifiers()
            } else if (currentStatus != PropertyPowerStatus.OFF) world.setBlockState(
                pos,
                state.withProperty(STATUS, PropertyPowerStatus.OFF)
            )

            if (canProcess()) process() else progressTicks = 0
            this.markDirtyClientEvery(5)
        }
    }


    override fun canProcess(): Boolean {
        return this.isMultiblockValid
                && !input[0].isEmpty
                && !input[1].isEmpty
                && !recipeOutput.isEmpty
                && (ItemStack.areItemsEqual(output[0], recipeOutput) || output[0].isEmpty)
                && output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
                && energyStorage.energyStored >= getModifiedEnergyCost()

    }

    override fun process() {
        if (progressTicks < getModifiedProcessTime()) {
            progressTicks++
        } else {
            progressTicks = 0

            var stacksize = recipeOutput.count
            var staticMultiplier = floor(productivityModifier).toInt()
            var randomMultiplier = if (productivityModifier - staticMultiplier > Math.random()) 1 else 0
            if (staticMultiplier != 0 || randomMultiplier != 0) {
                stacksize *= staticMultiplier + randomMultiplier
            }
            var outputStack = recipeOutput.copy()
            outputStack.count = stacksize
            output.setOrIncrement(0, outputStack)

            input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
            input.decrementSlot(1, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energyStorage.extractEnergy(getModifiedEnergyCost(), false)
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