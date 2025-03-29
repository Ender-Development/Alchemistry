package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.container.ContainerEvaporator
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityFluidDisplayWrapper
import io.enderdev.alchemistry.tiles.TileEvaporator
import net.minecraft.entity.player.InventoryPlayer

/**
 * Created by al132 on 4/29/2017.
 */

class GuiEvaporator(playerInv: InventoryPlayer, tile: TileEvaporator)
    : GuiBase<TileEvaporator>(ContainerEvaporator(playerInv, tile),tile, "evaporator") {

    init {
        this.displayData.add(CapabilityFluidDisplayWrapper(44, 21, 16, 70, tile::inputTank))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(36, tile.progressTicks, tile.recipeTime)
            this.drawTexturedModalRect(i + 70, j+118, 175, 0, k, 16)
        }
    }
}