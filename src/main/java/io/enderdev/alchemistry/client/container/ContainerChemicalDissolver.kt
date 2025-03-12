package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileChemicalDissolver
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerChemicalDissolver(playerInv: InventoryPlayer,
                                 tile: TileChemicalDissolver
) :
        ContainerBase<TileChemicalDissolver>(playerInv, tile) {

    override fun addOwnSlots() {
        addSlotToContainer(SlotItemHandler(tile.input, 0, 44, 100))
        addSlotArray(x_start = 98, y_start = 82, rows = 3, columns = 4, handler = tile.output)
    }
}