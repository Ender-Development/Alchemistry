package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileEvaporator
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler
import org.ender_development.catalyx.client.container.BaseContainer

class ContainerEvaporator(playerInv: InventoryPlayer, tile: TileEvaporator) : BaseContainer(playerInv, tile) {
	init {
		addSlotToContainer(SlotItemHandler(tile.output, 0, 116, 75))
	}
}
