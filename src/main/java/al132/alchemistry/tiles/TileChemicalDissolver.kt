package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.recipes.DissolverRecipe
import al132.alchemistry.recipes.register.DissolverRegister
import al132.alib.tiles.*
import al132.alib.utils.Utils.canStacksMerge
import al132.alib.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ITickable
import net.minecraftforge.common.util.Constants
import java.util.*

/**
 * Created by al132 on 1/16/2017.
 */
class TileChemicalDissolver : AbstractMachine<DissolverRecipe>(DissolverRegister.INSTANCE),
        IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.DISSOLVER.energyCapacity) {

    private var outputSuccessful = true
    private var outputBuffer: MutableList<ItemStack> = ArrayList()
    private var outputThisTick: ItemStack = ItemStack.EMPTY

    override val energyPerTick: Int
        get() = ConfigHandler.DISSOLVER.energyPerTick

    override val recipeTime: Int
        get() = ConfigHandler.DISSOLVER.processingTicks

    init {
        this.initInventoryCapability(1, 12)
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if(!this.getStackInSlot(slot).isEmpty) super.insertItem(slot, stack, simulate)
                else if (DissolverRecipe.match(stack, false) != null) super.insertItem(slot, stack, simulate)
                else stack
            }
        }
    }

    override fun updateRecipe(){
        this.currentRecipe = DissolverRecipe.match(input[0], true)
    }

    override fun onProcessComplete() {
        //if no output buffer, set the buffer to recipe outputs
        if (outputBuffer.isEmpty()) {
            outputBuffer = currentRecipe!!.outputs.calculateOutput().toMutableList()
            input.decrementSlot(0, currentRecipe!!.inputs[0].count)
        }
        //If output didn't happen or didn't fail last tick, queue up next output single stack
        if (outputSuccessful) {
            if (outputBuffer.isNotEmpty()) outputThisTick = outputBuffer[0].splitStack(ConfigHandler.DISSOLVER.speed)
            else outputThisTick = ItemStack.EMPTY

            if (outputBuffer.isNotEmpty() && outputBuffer[0].isEmpty) outputBuffer.removeAt(0)
            outputSuccessful = false
        }
        //Try to stack output with existing stacks in output, if possible
        for (i in 0 until output.slots) {
            if (canStacksMerge(outputThisTick, output[i], stacksCanbeEmpty = false)) {
                output.setOrIncrement(i, outputThisTick)
                outputSuccessful = true
                break
            }
        }
        //Otherwise try the empty stacks
        if (!outputSuccessful) {
            for (i in 0 until output.slots) {
                if (canStacksMerge(outputThisTick, output[i], stacksCanbeEmpty = true)) {
                    output.setOrIncrement(i, outputThisTick)
                    outputSuccessful = true
                    break
                }
            }
        }
        //consume single stack if successful, won't be designated as such until there's a "hit" above
        if (outputSuccessful) {
            outputThisTick = ItemStack.EMPTY
        }
    }

    override fun onWorkTick() {
        this.energyStorage.extractEnergy(energyPerTick, false)
    }

    override fun shouldTick(): Boolean {
        return !input[0].isEmpty || outputBuffer.isNotEmpty()
    }

    override fun shouldProcess(): Boolean {
        return energyStorage.energyStored >= energyPerTick
                && (currentRecipe != null || !outputBuffer.isEmpty())
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.outputSuccessful = compound.getBoolean("OutputSuccessful")

        val outputBufferList = compound.getTagList("OutputBuffer", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until outputBufferList.tagCount()) {
            outputBuffer.add(ItemStack(outputBufferList.getCompoundTagAt(i)))
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setBoolean("OutputSuccessful", this.outputSuccessful)

        val outputBufferList = NBTTagList()
        for (i in outputBuffer.indices) {
            val outputBufferEntry = NBTTagCompound()
            val tempStack = outputBuffer[i]

            tempStack.writeToNBT(outputBufferEntry)
            outputBufferList.appendTag(outputBufferEntry)
        }
        compound.setTag("OutputBuffer", outputBufferList)
        return compound
    }
}