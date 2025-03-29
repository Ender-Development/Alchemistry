package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import net.minecraft.inventory.IInventory
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/23/2017.
 */

class ContainerChemicalCombiner(playerInv: IInventory, tileCombiner: TileChemicalCombiner) :
        ContainerBase<TileChemicalCombiner>(playerInv, tileCombiner) {

    override fun addOwnSlots() {
        this.addSlotArray(x_start = 44, y_start = 39, rows = 3, columns = 3, handler = tile.input)
        this.addSlotToContainer(SlotItemHandler(tile.output, 0, 134, 57))
    }
}