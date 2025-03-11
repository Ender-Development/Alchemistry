package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.ElectrolyzerRecipe
import al132.alchemistry.recipes.ModRecipes
import al132.alchemistry.recipes.register.ElectrolyzerRegister
import al132.alib.tiles.*
import al132.alib.utils.extensions.containsItem
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 1/16/2017.
 */
class TileElectrolyzer : AbstractMachine<ElectrolyzerRecipe>(ElectrolyzerRegister.INSTANCE), IFluidTile,
    IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.ELECTROLYZER.energyCapacity) {

    val inputTank: FluidTank

    override val fluidTanks: FluidHandlerConcatenate?
        get() = FluidHandlerConcatenate(inputTank)

    override val recipeTime: Int
        get() = ConfigHandler.ELECTROLYZER.processingTicks

    override val energyPerTick: Int
        get() = ConfigHandler.ELECTROLYZER.energyPerTick

    override fun updateRecipe() {
        val inputStack = this.inputTank.fluid
        if ((inputStack != null) && (currentRecipe == null || currentRecipe!!.input.fluid == inputStack.fluid)) {
            this.currentRecipe = recipeRegister.firstOrNull { it.input.fluid == inputStack.fluid }
        }
        if (inputStack == null) currentRecipe = null
    }

    override fun onProcessComplete() {
        inputTank.drainInternal(currentRecipe!!.input.amount, true)

        if (world.rand.nextInt(100) < currentRecipe!!.electrolyteConsumptionChance) {
            input.decrementSlot(0, currentRecipe!!.electrolytes[0].count)
        }

        (0 until 4).forEach { output.setOrIncrement(it, currentRecipe!!.calculatedInSlot(it)) }
    }

    override fun onWorkTick() {
        this.energyStorage.extractEnergy(ConfigHandler.ELECTROLYZER.energyPerTick, false)
    }

    override fun shouldTick(): Boolean {
        return inputTank.fluidAmount > 0
    }

    override fun shouldProcess(): Boolean {
        return inputTank.fluidAmount >= currentRecipe!!.input.amount
                && input[0].count >= currentRecipe!!.electrolytes[0].count
                && this.energyStorage.energyStored >= energyPerTick
                && (0 until 4).all {
            val outputStack = output[it]
            val recipeStack = currentRecipe!!.outputs[it].copy()
            (outputStack.isEmpty || ItemStack.areItemsEqual(outputStack, recipeStack))
                    && outputStack.count + recipeStack.count <= recipeStack.maxStackSize
        }
    }

    init {
        this.initInventoryCapability(1, 4)

        inputTank = object : FluidTank(Fluid.BUCKET_VOLUME * 10) {
            override fun canFillFluidType(fluid: FluidStack?): Boolean {
                return recipeRegister.any { it.input.fluid == fluid?.fluid }
            }

            override fun onContentsChanged() {
                super.onContentsChanged()
                markDirtyGUI()
            }
        }

        inputTank.setTileEntity(this)
        inputTank.setCanFill(true)
        inputTank.setCanDrain(false)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (recipeRegister.any { it.electrolytes.containsItem(stack) })
                    super.insertItem(slot, stack, simulate)
                else
                    stack
            }
        }
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
}