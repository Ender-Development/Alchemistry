package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.container.ContainerFissionController
import io.enderdev.alchemistry.tiles.TileFissionController
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiFissionController(playerInv: InventoryPlayer, tile: TileFissionController) :
        GuiReactorController<TileFissionController>(ContainerFissionController(playerInv, tile), tile, textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "fission_controller_gui.png")
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(36, tile.progressTicks, tile.recipeTime)
            this.drawTexturedModalRect(i + 70, j + 75, 175, 0, k, 16)
        }
    }
}