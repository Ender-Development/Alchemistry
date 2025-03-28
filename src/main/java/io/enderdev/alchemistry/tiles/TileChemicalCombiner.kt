package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.CombinerRecipe
import io.enderdev.alchemistry.recipes.register.CombinerRegister
import io.enderdev.alchemistry.tiles.tags.EnergyTileImpl
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import io.enderdev.alchemistry.utils.extensions.get
import net.darkhax.gamestages.GameStageHelper
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by al132 on 1/22/2017.
 */
class TileChemicalCombiner : AbstractMachine<CombinerRecipe>(CombinerRegister.Companion.INSTANCE),
    IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.COMBINER.energyCapacity) {

    var recipeIsLocked = false
    val clientRecipeTarget: TileStackHandler
    var owner: String = ""

    override val energyPerTick: Int
        get() = ConfigHandler.COMBINER.energyPerTick

    override val recipeTime: Int
        get() = ConfigHandler.COMBINER.processingTicks

    init {
        initInventoryCapability(9, 1)
        clientRecipeTarget = object : TileStackHandler(1, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) = stack
            override fun extractItem(slot: Int, amount: Int, simulate: Boolean) = ItemStack.EMPTY
        }
    }

    override fun initInventoryInputCapability() {
        input = object : TileStackHandler(inputSlots, this) {
            override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
                return if (!recipeIsLocked || ItemStack.areItemsEqual(currentRecipe?.inputs?.get(slot) ?: ItemStack.EMPTY, stack))
                    super.insertItem(slot, stack, simulate)
                else return stack
            }

            override fun onContentsChanged(slot: Int) {
                if (!recipeIsLocked) updateRecipe()
            }
        }
    }

    override fun updateRecipe() {
        if (recipeIsLocked) return
        currentRecipe = CombinerRecipe.Companion.matchInputs(this.input)
    }

    override fun onProcessComplete() {
        currentRecipe?.let { output.setOrIncrement(0, it.output.copy()) }
        currentRecipe?.inputs?.forEachIndexed { index, stack ->
            if (!stack.isEmpty) {
                (input.decrementSlot(index, stack.count))
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
                && (!recipeIsLocked || ItemStack.areItemStacksEqual(CombinerRecipe.Companion.matchInputs(input)?.output ?: ItemStack.EMPTY, currentRecipe!!.output))
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
            this.currentRecipe = CombinerRecipe.Companion.matchOutput(recipeTarget)
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