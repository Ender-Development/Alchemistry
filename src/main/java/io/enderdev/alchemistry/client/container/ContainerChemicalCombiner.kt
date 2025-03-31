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
        addSlotArray(44, 39, 3, 3, tile.input)
        this.addSlotToContainer(SlotItemHandler(tile.output, 0, 134, 57))
    }
}