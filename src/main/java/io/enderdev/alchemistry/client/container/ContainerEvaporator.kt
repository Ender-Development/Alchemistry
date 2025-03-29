package io.enderdev.alchemistry.client.container

import io.enderdev.alchemistry.tiles.TileEvaporator
import net.minecraft.entity.player.InventoryPlayer
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by al132 on 4/29/2017.
 */
class ContainerEvaporator(playerInv: InventoryPlayer, tile: TileEvaporator) :
        ContainerBase<TileEvaporator>(playerInv, tile) {

    override fun addOwnSlots() {
        this.addSlotToContainer(SlotItemHandler(tile.output, 0, 116, 75))
    }
}