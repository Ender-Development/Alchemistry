package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.client.container.ContainerElectrolyzer
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityFluidDisplayWrapper
import io.enderdev.alchemistry.tiles.TileElectrolyzer
import net.minecraft.entity.player.InventoryPlayer

/**
 * Created by al132 on 1/16/2017.
 */
class GuiElectrolyzer(playerInv: InventoryPlayer, tile: TileElectrolyzer)
    : GuiBase<TileElectrolyzer>(ContainerElectrolyzer(playerInv, tile), tile, "electrolyzer") {

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
        this.displayData.add(CapabilityFluidDisplayWrapper(44, 21, 16, 70, tile::inputTank))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(36, tile.progressTicks, ConfigHandler.ELECTROLYZER.processingTicks)
            this.drawTexturedModalRect(i + 70, j + 56, 175, 0, k, 36)
        }
    }
}