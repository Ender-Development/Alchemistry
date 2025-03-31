package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.tiles.TileBase.Companion.ITEM_CAP
import io.enderdev.alchemistry.utils.extensions.get
import io.enderdev.alchemistry.utils.extensions.tryInsertInto
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.ItemStackHandler

open class TileStackHandler(size: Int, val tile: TileBase) : ItemStackHandler() {
	init {
		setSize(size)
	}

	override fun onContentsChanged(slot: Int) {
		super.onContentsChanged(slot)
		tile.markDirty()
	}

	fun clear() = (0..<slots).forEach { setStackInSlot(it, ItemStack.EMPTY) }

	fun incrementSlot(slot: Int, amountToAdd: Int) {
		val temp = this[slot]
		if (temp.count + amountToAdd <= temp.maxStackSize)
			temp.count += amountToAdd
		setStackInSlot(slot, temp)
	}

	fun setOrIncrement(slot: Int, stackToSet: ItemStack) {
		if(!stackToSet.isEmpty) {
			if (this[slot].isEmpty) setStackInSlot(slot, stackToSet)
			else incrementSlot(slot, stackToSet.count)
		}
	}

	fun decrementSlot(slot: Int, amount: Int) {
		val temp = this[slot]
		if (temp.isEmpty) return
		if (temp.count - amount < 0) return

		temp.shrink(amount)
		if (temp.count <= 0) this.setStackInSlot(slot, ItemStack.EMPTY)
		else this.setStackInSlot(slot, temp)
	}

	fun eject(direction: EnumFacing): Boolean {
		val originHandler = tile.getCapability(ITEM_CAP, direction)
		val targetHandler = tile.world.getTileEntity(tile.pos.offset(direction))
			?.getCapability(ITEM_CAP, direction.opposite)

		return if (originHandler != null && targetHandler != null) originHandler.tryInsertInto(targetHandler)
		else false
	}
}