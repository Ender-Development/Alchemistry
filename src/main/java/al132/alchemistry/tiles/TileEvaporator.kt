package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.EvaporatorRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alchemistry.recipes.register.EvaporatorRegister
import al132.alib.tiles.IFluidTile
import al132.alib.tiles.IGuiTile
import al132.alib.tiles.IItemTile
import al132.alib.utils.extensions.get
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.common.BiomeDictionary
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 4/29/2017.
 */
class TileEvaporator : AbstractMachine<EvaporatorRecipe>(EvaporatorRegister.INSTANCE), IFluidTile {

    val inputTank: FluidTank

    override val energyPerTick: Int
        get() = 0

    override val recipeTime: Int
        get() = calculateProcessingTime(ConfigHandler.EVAPORATOR.processingTicks)

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(inputTank)

    init {
        initInventoryCapability(0, 1)

        inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return recipeRegister.any { it.input.fluid == fluid?.fluid }
            }

            override fun onContentsChanged() {
                super.onContentsChanged()
                markDirtyClient()
            }
        }
        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }


    override fun updateRecipe() {
        val inputStack = this.inputTank.fluid
        if ((inputStack != null) && (currentRecipe == null || currentRecipe!!.input.fluid == inputStack.fluid)) {
            this.currentRecipe = recipeRegister.firstOrNull { it.input.fluid == inputStack.fluid }
        }
        if (inputStack == null) currentRecipe = null
    }

    override fun onProcessComplete() {
        output.setOrIncrement(0, currentRecipe!!.output.copy())
        inputTank.drainInternal(currentRecipe!!.input.amount, true)
    }

    override fun onWorkTick() {
        // NO-OP
    }

    override fun shouldTick(): Boolean {
        return inputTank.fluidAmount > 0
    }

    override fun shouldProcess(): Boolean {
        val recipeOutput = currentRecipe!!.output
        return inputTank.fluidAmount >= currentRecipe!!.input.amount
                && (inputTank.fluid == null || inputTank.fluid!!.fluid == currentRecipe!!.input.fluid)
                && (output[0].isEmpty || output[0].item == currentRecipe!!.output.item)
                && output[0].count + recipeOutput.count <= recipeOutput.maxStackSize
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
    }

    // TODO more elaborate calculation?
    private fun calculateProcessingTime(config: Int): Int {
        var temp = config
        if (!BiomeDictionary.hasType(world.getBiomeForCoordsBody(this.pos), BiomeDictionary.Type.DRY)) {
            temp += (config * .5).toInt()
        }
        return temp
    }
}