package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileLiquifier
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler


class ContainerLiquifier(playerInv: InventoryPlayer, tile: TileLiquifier) :
	ContainerBase<TileLiquifier>(playerInv, tile) {

	override fun addOwnSlots() {
		this.addSlotToContainer(SlotItemHandler(tile.input, 0, 44, 75))
	}
}
