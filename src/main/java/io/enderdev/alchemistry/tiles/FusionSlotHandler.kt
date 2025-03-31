package io.enderdev.alchemistry.tiles

import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class FusionSlotHandler(val tile: TileFusionController, itemHandler: IItemHandler, val index: Int, val xPos: Int, val yPos: Int) :
	SlotItemHandler(itemHandler, index, xPos, yPos) {

	override fun getSlotStackLimit() = if(this.tile.singleMode) 1 else super.getSlotStackLimit()
}
