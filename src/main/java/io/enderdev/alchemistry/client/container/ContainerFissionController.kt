package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileFissionController
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler
import org.ender_development.catalyx.client.container.BaseContainer

class ContainerFissionController(playerInv: InventoryPlayer, tile: TileFissionController) : BaseContainer(playerInv, tile) {
	init {
		addSlotToContainer(SlotItemHandler(tile.input, 0, 44, 75))
		addSlotArray(116, 75, 1, 2, tile.output)
	}
}
