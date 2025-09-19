package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.FusionSlotHandler
import io.enderdev.alchemistry.tiles.TileFusionController
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler
import org.ender_development.catalyx.client.container.BaseContainer

class ContainerFusionController(playerInv: InventoryPlayer, tile: TileFusionController) : BaseContainer(playerInv, tile) {
	init {
		addSlotToContainer(FusionSlotHandler(tile, tile.input, 0, 44, 75))
		addSlotToContainer(FusionSlotHandler(tile, tile.input, 1, 44 + 18, 75))
		addSlotToContainer(SlotItemHandler(tile.output, 0, 134, 75))
	}
}
