package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileElectrolyzer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler
import org.ender_development.catalyx.client.container.BaseContainer
import org.ender_development.catalyx.tiles.helper.TileStackHandler

class ContainerElectrolyzer(playerInv: InventoryPlayer, tile: TileElectrolyzer) : BaseContainer(playerInv, tile) {
	init {
		if(tile.input.slots < 1) {
			tile.input = TileStackHandler(1, tile)
		}
		addSlotToContainer(SlotItemHandler(tile.input, 0, 80, 39))
		addSlotArray(116, 57, 2, 2, tile.output)
	}
}
