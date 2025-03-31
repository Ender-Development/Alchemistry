package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.AtomizerRecipe
import io.enderdev.alchemistry.recipes.register.AtomizerRegister
import io.enderdev.alchemistry.tiles.tags.EnergyTileImpl
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import io.enderdev.alchemistry.tiles.tags.IFluidTile
import io.enderdev.alchemistry.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 4/29/2017.
 */
class TileAtomizer : AbstractMachine<AtomizerRecipe>(AtomizerRegister.Companion.INSTANCE), IFluidTile,
    IEnergyTile by EnergyTileImpl(ConfigHandler.ATOMIZER.energyCapacity) {

    val inputTank: FluidTank

    override val energyPerTick: Int
        get() = ConfigHandler.ATOMIZER.energyPerTick

    override val recipeTime: Int
        get() = ConfigHandler.ATOMIZER.processingTicks

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(inputTank)

    init {
        initInventoryCapability(0, 1)
        inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(with: FluidStack?) = fluid == null || fluid!!.fluid == with?.fluid

            override fun onContentsChanged() = markDirtyGUI()
        }

        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }

    override fun updateRecipe() {
        if (inputTank.fluid != null
            && (currentRecipe == null || !ItemStack.areItemStacksEqual(currentRecipe!!.output, output.getStackInSlot(0)))
        ) {
            currentRecipe = recipeRegister.firstOrNull { it.input.fluid == inputTank.fluid?.fluid }
        }
        if (inputTank.fluid == null) currentRecipe = null
    }

    override fun onProcessComplete() {
        output.setOrIncrement(0, currentRecipe!!.output.copy())
        inputTank.drainInternal(currentRecipe!!.input.amount, true)
    }

    override fun onWorkTick() {
        energyStorage.extractEnergy(energyPerTick, false)
    }

    override fun shouldTick() = inputTank.fluidAmount > 0

    override fun shouldProcess(): Boolean {
        val recipeOutput = currentRecipe!!.output
        return energyStorage.energyStored >= energyPerTick
                && inputTank.fluidAmount >= currentRecipe!!.input.amount
                && (ItemStack.areItemsEqual(output[0], recipeOutput) || output[0].isEmpty)
                && output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setTag("InputTankNBT", inputTank.writeToNBT(NBTTagCompound()))
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        inputTank.readFromNBT(compound.getCompoundTag("InputTankNBT"))
    }
}