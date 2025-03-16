package io.enderdev.alchemistry.client.gui

import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.CapabilityFluidDisplayWrapper
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.client.container.ContainerElectrolyzer
import io.enderdev.alchemistry.tiles.TileElectrolyzer
import net.minecraft.entity.player.InventoryPlayer

/**
 * Created by al132 on 1/16/2017.
 */
class GuiElectrolyzer(playerInv: InventoryPlayer, tile: TileElectrolyzer, override val displayNameOffset: Int = 51)
    : GuiBase<TileElectrolyzer>(ContainerElectrolyzer(playerInv, tile), tile, "electrolyzer") {

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 64, 16, 70, tile::energyStorage))
        this.displayData.add(CapabilityFluidDisplayWrapper(44, 64, 16, 70, tile::inputTank))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(36, tile.progressTicks, ConfigHandler.ELECTROLYZER.processingTicks)
            this.drawTexturedModalRect(i + 70, j+99, 175, 0, k, 36)
        }
    }
}