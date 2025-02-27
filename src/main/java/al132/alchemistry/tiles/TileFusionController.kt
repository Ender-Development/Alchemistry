package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.blocks.FusionControllerBlock
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
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by al132 on 4/29/2017.
 */
class TileFusionController(reactorType: ReactorType = ReactorType.FUSION) : AbstractReactorController(reactorType),
    IGuiTile, ITickable, IItemTile,
    IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.FUSION.energyCapacity) {

    var recipeOutput: ItemStack = ItemStack.EMPTY
    var singleMode: Boolean = false

    init {
        initInventoryCapability(2, 1)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                if (singleMode) {
                    if (this.getStackInSlot(slot).isEmpty) return super.insertItem(slot, stack, simulate)
                    else return stack
                }
                if (stack.item == ModItems.elements) {
                    return super.insertItem(slot, stack, simulate)
                } else return stack
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
            val isActive = !this.input[0].isEmpty && !this.input[1].isEmpty
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
            } else if (currentStatus != PropertyPowerStatus.OFF) world.setBlockState(
                pos,
                state.withProperty(STATUS, PropertyPowerStatus.OFF)
            )

            if (canProcess()) process()
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
                && energyStorage.energyStored >= ConfigHandler.FUSION.energyCapacity

    }

    override fun process() {
        if (progressTicks < ConfigHandler.FUSION.processingTicks) {
            progressTicks++
        } else {
            progressTicks = 0
            output.setOrIncrement(0, recipeOutput.copy())
            input.decrementSlot(0, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
            input.decrementSlot(1, 1) //Will refresh the recipe, clearing the recipeOutputs if only 1 stack is left
        }
        this.energyStorage.extractEnergy(ConfigHandler.FUSION.energyPerTick, false)
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setInteger("ProgressTicks", progressTicks)
        compound.setBoolean("singleMode", singleMode)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.progressTicks = compound.getInteger("ProgressTicks")
        this.singleMode = compound.getBoolean("singleMode")
        this.refreshRecipe()
        this.updateMultiblock()
    }
}