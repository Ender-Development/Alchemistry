package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.AtomizerRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alchemistry.recipes.register.AtomizerRegister
import al132.alib.tiles.*
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 4/29/2017.
 */
class TileAtomizer : AbstractMachine<AtomizerRecipe>(AtomizerRegister.INSTANCE), IFluidTile,
        IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.ATOMIZER.energyCapacity) {

    val inputTank: FluidTank
    override var recipeTime: Int = ConfigHandler.ATOMIZER.processingTicks
    var energyPerTick: Int = ConfigHandler.ATOMIZER.energyPerTick

    init {
        initInventoryCapability(0, 1)
        inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return if(this.fluid == null)
                    true
                else
                    this.fluid!!.fluid == fluid?.fluid
            }

            override fun onContentsChanged() {
                updateRecipe()
                markDirtyGUI()
            }
        }

        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }

    override fun updateRecipe() {
        if (inputTank.fluid != null &&
                (currentRecipe == null || !ItemStack.areItemStacksEqual(currentRecipe!!.output, output.getStackInSlot(0)))) {
            currentRecipe = AtomizerRegister.INSTANCE.recipes.firstOrNull { it.input.fluid == inputTank.fluid?.fluid }
        }
        if (inputTank.fluid == null) currentRecipe = null
    }

    override fun onProcessComplete() {
        output.setOrIncrement(0, currentRecipe!!.output.copy())
        inputTank.drainInternal(currentRecipe!!.input.amount, true)
    }

    override fun onWorkTick() {
        this.energyStorage.extractEnergy(energyPerTick, false)
    }

    override fun shouldTick(): Boolean {
        return inputTank.fluidAmount > 0
    }

    override fun shouldProcess(): Boolean {
        if (currentRecipe != null) {
            val recipeOutput = currentRecipe!!.output
            return energyStorage.energyStored >= energyPerTick
                    && inputTank.fluidAmount >= currentRecipe!!.input.amount
                    && (ItemStack.areItemsEqual(output[0], recipeOutput) || output[0].isEmpty)
                    && output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
        } else return false;
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        val inputTankNBT = NBTTagCompound()
        this.inputTank.writeToNBT(inputTankNBT)
        compound.setTag("InputTankNBT", inputTankNBT)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.inputTank.readFromNBT(compound.getCompoundTag("InputTankNBT"))
        updateRecipe()
    }

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(inputTank)
}