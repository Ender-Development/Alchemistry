package al132.alchemistry.client

import al132.alchemistry.tiles.TileEvaporator
import al132.alib.client.CapabilityFluidDisplayWrapper
import al132.alib.utils.Translator
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 4/29/2017.
 */

class GuiEvaporator(playerInv: InventoryPlayer, tile: TileEvaporator, override val displayNameOffset: Int = 51)
    : GuiBase<TileEvaporator>(ContainerEvaporator(playerInv, tile),tile, textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "evaporator_gui.png")
    }

    override val displayName = Translator.translateToLocal("tile.evaporator.name")

    init {
        this.displayData.add(CapabilityFluidDisplayWrapper(44, 64, 16, 70, tile::inputTank))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(36, tile.progressTicks, tile.calculateProcessingTime())
            this.drawTexturedModalRect(i + 70, j+118, 175, 0, k, 16)
        }
    }
}