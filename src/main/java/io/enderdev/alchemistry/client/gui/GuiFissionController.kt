package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.container.ContainerFissionController
import io.enderdev.alchemistry.tiles.TileFissionController
import net.minecraft.entity.player.InventoryPlayer

/**
 * Created by al132 on 1/16/2017.
 */
class GuiFissionController(playerInv: InventoryPlayer, tile: TileFissionController) :
        GuiReactorController<TileFissionController>(ContainerFissionController(playerInv, tile), tile, "fission_controller") {

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        drawProgressBar(70, 75, 175, 0, 36, 16)
    }
}