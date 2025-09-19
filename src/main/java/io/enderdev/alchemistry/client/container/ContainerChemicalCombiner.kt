package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.SlotItemHandler
import org.ender_development.catalyx.client.container.BaseContainer

class ContainerChemicalCombiner(playerInv: IInventory, val tile: TileChemicalCombiner) : BaseContainer(playerInv, tile) {
	init {
		addSlotArray(44, 39, 3, 3, tile.input)
		addSlotToContainer(SlotItemHandler(tile.output, 0, 134, 57))
	}
}
