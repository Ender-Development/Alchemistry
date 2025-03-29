package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.client.container.ContainerLiquifier
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityFluidDisplayWrapper
import io.enderdev.alchemistry.tiles.TileLiquifier
import net.minecraft.entity.player.InventoryPlayer

/**
 * Created by al132 on 1/16/2017.
 */
class GuiLiquifier(playerInv: InventoryPlayer, tile: TileLiquifier) :
        GuiBase<TileLiquifier>(ContainerLiquifier(playerInv, tile), tile, "liquifier") {

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
        this.displayData.add(CapabilityFluidDisplayWrapper(116, 21, 16, 70, tile::outputTank))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(36, tile.progressTicks, ConfigHandler.LIQUIFIER.processingTicks)
            this.drawTexturedModalRect(i + 70, j+118, 175, 0, k, 16)
        }
    }
}