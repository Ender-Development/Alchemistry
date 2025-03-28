package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileElectrolyzer
import io.enderdev.alchemistry.tiles.TileStackHandler
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerElectrolyzer(playerInv: InventoryPlayer, tile: TileElectrolyzer) :
        ContainerBase<TileElectrolyzer>(playerInv, tile) {

    override fun addOwnSlots() {
        if (tile.input.slots < 1) {
            tile.input = TileStackHandler(1, tile)
        }
        addSlotToContainer(SlotItemHandler(tile.input, 0, 80, 82))
        addSlotArray(x_start = 116, y_start = 100, rows = 2, columns = 2, handler = tile.output)
    }
}