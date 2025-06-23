package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileAtomizer
import io.enderdev.catalyx.client.container.BaseContainer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

class ContainerAtomizer(playerInv: InventoryPlayer, tile: TileAtomizer) : BaseContainer(playerInv, tile) {
	override fun addOwnSlots() {
		this.addSlotToContainer(SlotItemHandler(tile.output, 0, 116, 75))
	}
}
