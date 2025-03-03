package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.blocks.FissionControllerBlock.Companion.STATUS
import al132.alchemistry.blocks.ModBlocks
import al132.alchemistry.blocks.PropertyPowerStatus.*
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ModItems
import al132.alib.tiles.*
import al132.alib.utils.extensions.get
import al132.alib.utils.extensions.toStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import kotlin.math.floor

/**
 * Created by al132 on 4/29/2017.
 */
class TileFissionController(reactorType: ReactorType = ReactorType.FISSION,
                            override val defaultEnergyPerTick: Int = ConfigHandler.FISSION.energyPerTick,
                            override val defaultEnergyCapacity: Int = ConfigHandler.FISSION.energyCapacity,
                            override val defaultProcessTime: Int = ConfigHandler.FISSION.processingTicks
) : AbstractReactorController(reactorType),
    IGuiTile, ITickable, IItemTile,
    IEnergyTile by EnergyTileImpl(capacity = defaultEnergyCapacity) {

    var recipeOutput1: ItemStack = ItemStack.EMPTY
    var recipeOutput2: ItemStack = ItemStack.EMPTY

    init {
        initInventoryCapability(1, 2)
        loadConfig(ConfigHandler.FISSION.fissionReactorModifiers)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (stack.item == ModItems.elements && stack.metadata > 1) {
                    return super.insertItem(slot, stack, simulate)
                } else return stack
            }

            override fun onContentsChanged(slot: Int) {
                (tile as TileFissionController).refreshRecipe()
                super.onContentsChanged(slot)
            }
        }
    }

    override fun refreshRecipe() {
        val meta = this.input[0].metadata
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


    override fun update() {
        if (!world.isRemote) {
            val isActive = !this.input[0].isEmpty && energyStorage.energyStored >= getModifiedEnergyCost()
            checkMultiblockTicks++
            if (checkMultiblockTicks >= 20) {
                updateMultiblock()
                checkMultiblockTicks = 0
            }
            val state = this.world.getBlockState(this.pos)
            if (state.block != ModBlocks.fissionController) return;
            val currentStatus = state.getValue(STATUS)
            if (this.isMultiblockValid) {
                if (isActive) {
                    if (currentStatus != ON) this.world.setBlockState(this.pos, state.withProperty(STATUS, ON))
                } else if (currentStatus != STANDBY) world.setBlockState(pos, state.withProperty(STATUS, STANDBY))
                updateModifiers()
            } else if (currentStatus != OFF) world.setBlockState(pos, state.withProperty(STATUS, OFF))

            if (canProcess()) process()
            this.markDirtyClientEvery(5)
        }
    }

    override fun canProcess(): Boolean {
        return this.isMultiblockValid
                && !recipeOutput1.isEmpty
                && (ItemStack.areItemsEqual(output[0], recipeOutput1) || output[0].isEmpty)
                && (ItemStack.areItemsEqual(output[1], recipeOutput2) || output[1].isEmpty)
                && output[0].count + recipeOutput1.count <= recipeOutput1.maxStackSize
                && output[1].count + recipeOutput2.count <= recipeOutput2.maxStackSize
                && energyStorage.energyStored >= getModifiedEnergyCost()
    }

    override fun process() {
        if (progressTicks < getModifiedProcessTime()) {
            progressTicks++
        } else {
            progressTicks = 0

            var stacksize1 = recipeOutput1.count
            var staticMultiplier = floor(productivityModifier).toInt()
            var randomMultiplier = if (productivityModifier - staticMultiplier > Math.random()) 1 else 0
            if (staticMultiplier != 0 || randomMultiplier != 0) {
                stacksize1 *= staticMultiplier + randomMultiplier
            }
            var outputStack1 = recipeOutput1.copy()
            outputStack1.count = if (stacksize1 > outputStack1.maxStackSize) outputStack1.maxStackSize else stacksize1
            output.setOrIncrement(0, outputStack1)
            if (!recipeOutput2.isEmpty) {
                var stacksize2 = recipeOutput2.count
                if (staticMultiplier != 0 || randomMultiplier != 0) {
                    stacksize2 *= staticMultiplier + randomMultiplier
                }
                var outputStack2 = recipeOutput2.copy()
                outputStack2.count = if (stacksize2 > outputStack2.maxStackSize) outputStack2.maxStackSize else stacksize2
                output.setOrIncrement(1, outputStack2)
            }
            input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energyStorage.extractEnergy(getModifiedEnergyCost(), false)
    }
}