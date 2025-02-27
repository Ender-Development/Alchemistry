package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.blocks.FissionControllerBlock
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
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by al132 on 4/29/2017.
 */
class TileFissionController(reactorType: ReactorType = ReactorType.FISSION) : AbstractReactorController(reactorType),
    IGuiTile, ITickable, IItemTile,
    IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.FISSION.energyCapacity) {

    var recipeOutput1: ItemStack = ItemStack.EMPTY
    var recipeOutput2: ItemStack = ItemStack.EMPTY

    init {
        initInventoryCapability(1, 2)
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
            val isActive = !this.input[0].isEmpty
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
                && energyStorage.energyStored >= ConfigHandler.FISSION.energyPerTick
    }

    override fun process() {
        if (progressTicks < ConfigHandler.FISSION.processingTicks) {
            progressTicks++
        } else {
            progressTicks = 0
            output.setOrIncrement(0, recipeOutput1.copy())
            if (!recipeOutput2.isEmpty) output.setOrIncrement(1, recipeOutput2.copy())
            input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energyStorage.extractEnergy(ConfigHandler.FISSION.energyPerTick, false)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setInteger("ProgressTicks", progressTicks)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.progressTicks = compound.getInteger("ProgressTicks")
        this.refreshRecipe()
        this.updateMultiblock()
    }
}