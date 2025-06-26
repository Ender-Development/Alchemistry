package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileEvaporator
import io.enderdev.catalyx.client.container.BaseContainer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

class ContainerEvaporator(playerInv: InventoryPlayer, tile: TileEvaporator) : BaseContainer(playerInv, tile) {
	init {
		addSlotToContainer(SlotItemHandler(tile.output, 0, 116, 75))
	}
}
