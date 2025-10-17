package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.client.button.LockButtonWrapper
import io.enderdev.alchemistry.recipes.CombinerRecipe
import net.darkhax.gamestages.GameStageHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item.getByNameOrId
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.items.ItemStackHandler
import org.ender_development.catalyx.client.button.AbstractButtonWrapper
import org.ender_development.catalyx.tiles.BaseMachineTile
import org.ender_development.catalyx.tiles.helper.EnergyTileImpl
import org.ender_development.catalyx.tiles.helper.ICopyPasteExtraTile
import org.ender_development.catalyx.tiles.helper.IEnergyTile
import org.ender_development.catalyx.tiles.helper.TileStackHandler
import org.ender_development.catalyx.utils.extensions.get
import org.ender_development.catalyx.utils.extensions.toStack

class TileChemicalCombiner : BaseMachineTile<CombinerRecipe>(Alchemistry), IEnergyTile by EnergyTileImpl(ConfigHandler.COMBINER.energyCapacity), ICopyPasteExtraTile {
	var recipeIsLocked = false
	val clientRecipeTarget: TileStackHandler
	var owner: String = ""

	override val energyPerTick = ConfigHandler.COMBINER.energyPerTick

	override val recipeTime = ConfigHandler.COMBINER.processingTicks

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
				return if(!recipeIsLocked || ItemStack.areItemsEqual(currentRecipe?.inputs?.get(slot) ?: ItemStack.EMPTY, stack))
					super.insertItem(slot, stack, simulate)
				else return stack
			}

			override fun onContentsChanged(slot: Int) {
				if(!recipeIsLocked) updateRecipe()
			}
		}
	}

	override fun updateRecipe() {
		if(recipeIsLocked) return
		currentRecipe = CombinerRecipe.Companion.matchInputs(input)
	}

	override fun onProcessComplete() {
		currentRecipe?.let { output.setOrIncrement(0, it.output.copy()) }
		currentRecipe?.inputs?.forEachIndexed { index, stack ->
			if(!stack.isEmpty) {
				(input.decrementSlot(index, stack.count))
			}
		}
	}

	override fun onWorkTick() {
		energyStorage.extractEnergy(energyPerTick, false)
	}

	override fun onIdleTick() {
		super.onIdleTick()
		if(recipeIsLocked) clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output?.copy()) ?: ItemStack.EMPTY)
	}

	override fun shouldTick() = energyStorage.energyStored >= energyPerTick

	override fun shouldProcess(): Boolean {
		return (currentRecipe!!.gamestage == "" || hasCurrentRecipeStage())
				&& (currentRecipe!!.output.count + output[0].count <= currentRecipe!!.output.maxStackSize) //output quantities can stack
				&& (ItemStack.areItemsEqual(output[0], currentRecipe!!.output) || output[0].isEmpty) //output item types can stack
				&& currentRecipe!!.matchesHandlerStacks(input)
				&& (!recipeIsLocked || ItemStack.areItemStacksEqual(CombinerRecipe.Companion.matchInputs(input)?.output ?: ItemStack.EMPTY, currentRecipe!!.output))
	}

	private fun hasCurrentRecipeStage() =
		if(Loader.isModLoaded("gamestages")) {
			val playerList = FMLCommonHandler.instance().minecraftServerInstance.playerList
			val playerOwner: EntityPlayerMP = playerList.getPlayerByUsername(owner) ?: return false
			GameStageHelper.hasStage(playerOwner, currentRecipe?.gamestage)
		} else true

	override fun readFromNBT(compound: NBTTagCompound) {
		super.readFromNBT(compound)
		recipeIsLocked = compound.getBoolean("RecipeIsLocked")
		owner = compound.getString("Owner")

		if(recipeIsLocked) {
			// TODO: why does this tempItemHandler even exist?
			val tempItemHandler = ItemStackHandler(9)
			val recipeInputsList = compound.getTagList("RecipeInputs", Constants.NBT.TAG_COMPOUND)
			for(i in 0..<recipeInputsList.tagCount()) {
				tempItemHandler.setStackInSlot(i, ItemStack(recipeInputsList.getCompoundTagAt(i)))
			}
			val recipeTarget = ItemStack(compound.getCompoundTag("RecipeTarget"))
			currentRecipe = CombinerRecipe.Companion.matchOutput(recipeTarget)
			clientRecipeTarget.setStackInSlot(0, (currentRecipe?.output?.copy()) ?: ItemStack.EMPTY)
		} else {
			clientRecipeTarget.setStackInSlot(0, ItemStack.EMPTY)
		}
	}

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
		compound.setBoolean("RecipeIsLocked", recipeIsLocked)
		compound.setString("Owner", owner)
		if(recipeIsLocked && currentRecipe != null) {
			val recipeInputs = NBTTagList()
			for(i in currentRecipe!!.inputs.indices) {
				val recipeInputEntry = NBTTagCompound()
				val tempStack = currentRecipe!!.inputs[i].copy()
				tempStack.writeToNBT(recipeInputEntry)
				recipeInputs.appendTag(recipeInputEntry)
			}
			compound.setTag("RecipeInputs", recipeInputs)
		}
		compound.setTag("RecipeTarget", clientRecipeTarget[0].serializeNBT())
		return super.writeToNBT(compound)
	}

	override fun handleButtonPress(button: AbstractButtonWrapper) {
		if(button is LockButtonWrapper)
			if(recipeIsLocked) {
				recipeIsLocked = false
				currentRecipe = null
			} else
				recipeIsLocked = true
		super.handleButtonPress(button)
	}

	// ICopyPasteExtraTile

	override fun copyData(tag: NBTTagCompound) =
		tag.setString("RecipeOutput", currentRecipe?.output?.string() ?: "")

	override fun pasteData(tag: NBTTagCompound, player: EntityPlayer) {
		if(tag.hasKey("RecipeOutput")) {
			val output = tag.getString("RecipeOutput")
			if(output.isEmpty()) {
				recipeIsLocked = false
				currentRecipe = null
				clientRecipeTarget.setStackInSlot(0, ItemStack.EMPTY)
			} else
				output.stack()?.let {
					currentRecipe = CombinerRecipe.matchOutput(it)
					recipeIsLocked = currentRecipe != null
					if(currentRecipe != null)
						clientRecipeTarget.setStackInSlot(0, it.copy())
				}
		}
	}

	private fun ItemStack.string() =
		"${item.registryName}$$metadata"

	private fun String.stack(): ItemStack? {
		val (name, meta) = split('$')
		return getByNameOrId(name)?.toStack(meta = meta.toInt())
	}
}
