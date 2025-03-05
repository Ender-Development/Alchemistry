package al132.alchemistry.client

import al132.alchemistry.tiles.TileFissionController
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 1/16/2017.
 */
class ContainerFissionController(playerInv: InventoryPlayer,
                                 tile: TileFissionController) :
        ContainerBase<TileFissionController>(playerInv, tile) {

    override fun addOwnSlots() {
        this.addSlotToContainer(SlotItemHandler(tile.input, 0, 44, 75))
        this.addSlotArray(116, 75, 1, 2, tile.output)
    }
}