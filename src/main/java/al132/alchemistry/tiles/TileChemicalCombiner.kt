package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.items.ModItems
import al132.alchemistry.recipes.CombinerRecipe
import al132.alchemistry.recipes.register.CombinerRegister
import al132.alib.tiles.*
import al132.alib.utils.extensions.areItemStacksEqual
import al132.alib.utils.extensions.areItemsEqual
import al132.alib.utils.extensions.get
import net.darkhax.gamestages.GameStageHelper
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ITickable
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by al132 on 1/22/2017.
 */
class TileChemicalCombiner : AbstractMachine<CombinerRecipe>(CombinerRegister.INSTANCE),
    IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.COMBINER.energyCapacity) {

    var recipeIsLocked = false
    val clientRecipeTarget: ALTileStackHandler
    var owner: String = ""

    override val energyPerTick: Int
        get() = ConfigHandler.COMBINER.energyPerTick

    override val recipeTime: Int
        get() = ConfigHandler.COMBINER.processingTicks

    init {
        initInventoryCapability(9, 1)
        clientRecipeTarget = object : ALTileStackHandler(1, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = stack
            override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY
        }
    }

    override fun initInventoryInputCapability() {
        input = object : ALTileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (!recipeIsLocked || currentRecipe?.inputs?.get(slot)
                        ?.areItemsEqual(stack) == true
                ) super.insertItem(slot, stack, simulate)
                else return stack
            }

            override fun onContentsChanged(slot: Int) {
                if (!recipeIsLocked) updateRecipe()
            }
        }
    }

    override fun updateRecipe() {
        if (recipeIsLocked) return
        currentRecipe = CombinerRecipe.matchInputs(this.input)
    }

    override fun onProcessComplete() {
        currentRecipe?.let { output.setOrIncrement(0, it.output.copy()) }
        currentRecipe?.inputs?.forEachIndexed { index, stack ->
            if (!stack.isEmpty) {
                (input.decrementSlot(index, stack.count))
            }
            if (input.getStackInSlot(index).item == ModItems.slotFiller) {
                input.decrementSlot(index, 1)
            }
        }
    }

    override fun onWorkTick() {
        this.energyStorage.extractEnergy(energyPerTick, false)
    }

    override fun onIdleTick() {
        super.onIdleTick()
        if (recipeIsLocked) clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output?.copy()) ?: ItemStack.EMPTY)
    }

    override fun shouldTick(): Boolean {
        return energyStorage.energyStored >= energyPerTick
    }

    override fun shouldProcess(): Boolean {
        return (currentRecipe!!.gamestage == "" || hasCurrentRecipeStage())
                && (currentRecipe!!.output.count + output[0].count <= currentRecipe!!.output.maxStackSize) //output quantities can stack
                && (ItemStack.areItemsEqual(output[0], currentRecipe!!.output) || output[0].isEmpty) //output item types can stack
                && currentRecipe!!.matchesHandlerStacks(this.input)
                && (!recipeIsLocked || CombinerRecipe.matchInputs(input)?.output?.areItemStacksEqual(currentRecipe!!.output) == true)
    }

    private fun hasCurrentRecipeStage(): Boolean {
        if (Loader.isModLoaded("gamestages")) {
            val playerList = FMLCommonHandler.instance().minecraftServerInstance.playerList
            val playerOwner: EntityPlayerMP = playerList.getPlayerByUsername(owner) ?: return false
            return GameStageHelper.hasStage(playerOwner, currentRecipe?.gamestage)
        } else return true
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.recipeIsLocked = compound.getBoolean("RecipeIsLocked")
        this.owner = compound.getString("Owner")

        if (this.recipeIsLocked) {
            val tempItemHandler = ItemStackHandler(9)
            val recipeInputsList = compound.getTagList("RecipeInputs", Constants.NBT.TAG_COMPOUND)
            for (i in 0 until recipeInputsList.tagCount()) {
                tempItemHandler.setStackInSlot(i, ItemStack(recipeInputsList.getCompoundTagAt(i)))
            }
            val recipeTarget = ItemStack(compound.getCompoundTag("RecipeTarget"))
            this.currentRecipe = CombinerRecipe.matchOutput(recipeTarget)
            clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output?.copy()) ?: ItemStack.EMPTY!!)
        } else {
            clientRecipeTarget.setStackInSlot(0, ItemStack.EMPTY)
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setBoolean("RecipeIsLocked", this.recipeIsLocked)
        compound.setString("Owner", this.owner)
        if (this.recipeIsLocked && this.currentRecipe != null) {
            val recipeInputs = NBTTagList()
            for (i in this.currentRecipe!!.inputs.indices) {
                val recipeInputEntry = NBTTagCompound()
                val tempStack = this.currentRecipe!!.inputs[i].copy()
                tempStack.writeToNBT(recipeInputEntry)
                recipeInputs.appendTag(recipeInputEntry)
            }
            compound.setTag("RecipeInputs", recipeInputs)
        }
        compound.setTag("RecipeTarget", clientRecipeTarget[0].serializeNBT())
        return super.writeToNBT(compound)
    }
}