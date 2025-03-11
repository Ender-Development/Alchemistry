package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.LiquifierRecipe
import al132.alchemistry.recipes.register.LiquifierRegister
import al132.alib.tiles.ALTileStackHandler
import al132.alib.tiles.EnergyTileImpl
import al132.alib.tiles.IEnergyTile
import al132.alib.tiles.IFluidTile
import al132.alib.utils.extensions.areItemsEqual
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 4/29/2017.
 */
class TileLiquifier : AbstractMachine<LiquifierRecipe>(LiquifierRegister.INSTANCE), IFluidTile,
    IEnergyTile by EnergyTileImpl(ConfigHandler.LIQUIFIER.energyCapacity) {

    val outputTank: FluidTank

    override val energyPerTick: Int
        get() = ConfigHandler.LIQUIFIER.energyPerTick

    override val recipeTime: Int
        get() = ConfigHandler.LIQUIFIER.processingTicks

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(outputTank)

    init {
        initInventoryCapability(1, 0)
        outputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return recipeRegister.any { it.output.fluid == fluid?.fluid }
            }

            override fun onContentsChanged() {
                super.onContentsChanged()
                markDirtyGUI()
                updateRecipe()
            }
        }
        outputTank.setTileEntity(this)
        outputTank.setCanFill(false)
        outputTank.setCanDrain(true)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (recipeRegister.any { it.input.isItemEqual(stack) }) super.insertItem(slot, stack, simulate)
                else stack
            }

            override fun onContentsChanged(slot: Int) {
                updateRecipe()
                markDirtyGUI()
            }
        }
    }

    override fun updateRecipe() {
        val inputStack = this.input.getStackInSlot(0)
        if (!inputStack.isEmpty
            && (currentRecipe == null || !ItemStack.areItemStacksEqual(currentRecipe!!.input, inputStack))) {
            this.currentRecipe = recipeRegister.firstOrNull { it.input.areItemsEqual(inputStack) }
        }
        if (inputStack.isEmpty) currentRecipe = null
    }

    override fun onProcessComplete() {
        outputTank.fillInternal(currentRecipe!!.output.copy(), true)
        input[0].shrink(currentRecipe!!.input.count)
    }

    override fun onWorkTick() {
        this.energyStorage.extractEnergy(energyPerTick, false)
    }

    override fun shouldTick(): Boolean {
        return !this.input[0].isEmpty
    }

    override fun shouldProcess(): Boolean {
        val recipeOutput = currentRecipe!!.output
        return (outputTank.capacity >= outputTank.fluidAmount + recipeOutput.amount
                && this.energyStorage.energyStored >= energyPerTick
                && input[0].count >= currentRecipe!!.input.count
                && ((outputTank.fluid?.fluid == (recipeOutput.fluid?: false)) || outputTank.fluid == null))
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        val outputTankNBT = NBTTagCompound()
        this.outputTank.writeToNBT(outputTankNBT)
        compound.setTag("OutputTankNBT", outputTankNBT)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.outputTank.readFromNBT(compound.getCompoundTag("OutputTankNBT"))
        updateRecipe()
    }
}