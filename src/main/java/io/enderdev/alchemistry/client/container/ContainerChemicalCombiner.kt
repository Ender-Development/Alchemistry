package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import io.enderdev.catalyx.client.container.BaseContainer
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.SlotItemHandler

class ContainerChemicalCombiner(playerInv: IInventory, val tile: TileChemicalCombiner) : BaseContainer(playerInv, tile) {
	init {
		addSlotArray(44, 39, 3, 3, tile.input)
		addSlotToContainer(SlotItemHandler(tile.output, 0, 134, 57))
	}
}
