package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileChemicalDissolver
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler
import org.ender_development.catalyx.client.container.BaseContainer

class ContainerChemicalDissolver(playerInv: InventoryPlayer, tile: TileChemicalDissolver) : BaseContainer(playerInv, tile) {
	init {
		addSlotToContainer(SlotItemHandler(tile.input, 0, 44, 57))
		addSlotArray(98, 39, 3, 4, tile.output)
	}
}
