package al132.alchemistry.client

import al132.alchemistry.tiles.TileChemicalDissolver
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerChemicalDissolver(playerInv: InventoryPlayer,
                                 tile: TileChemicalDissolver) :
        ContainerBase<TileChemicalDissolver>(playerInv, tile) {

    override fun addOwnSlots() {
        addSlotToContainer(SlotItemHandler(tile.input, 0, 44, 53))
        addSlotArray(x_start = 116, y_start = 17, rows = 5, columns = 2, handler = tile.output)
    }
}