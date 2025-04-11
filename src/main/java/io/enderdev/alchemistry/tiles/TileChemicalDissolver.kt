package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.recipes.DissolverRecipe
import io.enderdev.alchemistry.recipes.register.DissolverRegister
import io.enderdev.alchemistry.tiles.tags.EnergyTileImpl
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import io.enderdev.alchemistry.utils.extensions.canMergeWith
import io.enderdev.alchemistry.utils.extensions.get
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants

class TileChemicalDissolver : AbstractMachine<DissolverRecipe>(DissolverRegister.Companion.INSTANCE),
	IEnergyTile by EnergyTileImpl(capacity = ConfigHandler.DISSOLVER.energyCapacity) {

	private var outputSuccessful = true
	private var outputBuffer: MutableList<ItemStack> = ArrayList()
	private var outputThisTick: ItemStack = ItemStack.EMPTY

	override val energyPerTick: Int
		get() = ConfigHandler.DISSOLVER.energyPerTick

	override val recipeTime: Int
		get() = ConfigHandler.DISSOLVER.processingTicks

	init {
		initInventoryCapability(1, 12)
	}

	override fun initInventoryInputCapability() {
		input = object : TileStackHandler(inputSlots, this) {
			override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean) =
				if(!getStackInSlot(slot).isEmpty || DissolverRecipe.Companion.match(stack, false) != null)
					super.insertItem(slot, stack, simulate)
				else stack
		}
	}

	override fun updateRecipe() {
		currentRecipe = DissolverRecipe.Companion.match(input[0], true)
	}

	fun tryOutput() {
		//If output didn't happen or didn't fail last tick, queue up next output single stack
		if(outputSuccessful) {
			if(outputBuffer.isNotEmpty()) outputThisTick = outputBuffer[0].splitStack(ConfigHandler.DISSOLVER.speed)
			else outputThisTick = ItemStack.EMPTY

			if(outputBuffer.isNotEmpty() && outputBuffer[0].isEmpty) outputBuffer.removeAt(0)
			outputSuccessful = false
		}
		//Try to stack output with existing stacks in output, if possible
		for(i in 0..<output.slots) {
			if(outputThisTick.canMergeWith(output[i], false)) {
				output.setOrIncrement(i, outputThisTick)
				outputSuccessful = true
				break
			}
		}
		//Otherwise try the empty stacks
		if(!outputSuccessful) {
			for(i in 0..<output.slots) {
				if(outputThisTick.canMergeWith(output[i], true)) {
					output.setOrIncrement(i, outputThisTick)
					outputSuccessful = true
					break
				}
			}
		}
		//consume single stack if successful, won't be designated as such until there's a "hit" above
		if(outputSuccessful)
			outputThisTick = ItemStack.EMPTY
	}

	override fun onProcessComplete() {
		//if no output buffer, set the buffer to recipe outputs
		if(outputBuffer.isEmpty()) {
			outputBuffer = currentRecipe!!.outputs.calculateOutput().toMutableList()
			input.decrementSlot(0, currentRecipe!!.inputs[0].count)
		}

		tryOutput()
	}

	override fun onIdleTick() {
		super.onIdleTick()
		tryOutput()
	}

	override fun onWorkTick() {
		energyStorage.extractEnergy(energyPerTick, false)
	}

	override fun shouldTick() = !input[0].isEmpty || outputBuffer.isNotEmpty()

	override fun shouldProcess() =
		energyStorage.energyStored >= energyPerTick && (currentRecipe != null || !outputBuffer.isEmpty())

	override fun readFromNBT(compound: NBTTagCompound) {
		super.readFromNBT(compound)
		outputSuccessful = compound.getBoolean("OutputSuccessful")

		val outputBufferList = compound.getTagList("OutputBuffer", Constants.NBT.TAG_COMPOUND)
		for(i in 0..<outputBufferList.tagCount())
			outputBuffer.add(ItemStack(outputBufferList.getCompoundTagAt(i)))
	}

	override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(compound)
		compound.setBoolean("OutputSuccessful", outputSuccessful)

		val outputBufferList = NBTTagList()
		for(i in outputBuffer.indices) {
			val outputBufferEntry = NBTTagCompound()
			val tempStack = outputBuffer[i]

			tempStack.writeToNBT(outputBufferEntry)
			outputBufferList.appendTag(outputBufferEntry)
		}
		compound.setTag("OutputBuffer", outputBufferList)
		return compound
	}
}
