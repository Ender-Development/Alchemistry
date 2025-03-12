package al132.alchemistry.client

import al132.alchemistry.ConfigHandler
import al132.alchemistry.client.container.ContainerElectrolyzer
import al132.alchemistry.tiles.TileElectrolyzer
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.CapabilityFluidDisplayWrapper
import al132.alib.utils.Translator
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiElectrolyzer(playerInv: InventoryPlayer, tile: TileElectrolyzer, override val displayNameOffset: Int = 51)
    : GuiBase<TileElectrolyzer>(ContainerElectrolyzer(playerInv, tile), tile, textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "electrolyzer_gui.png")
    }

    override val displayName = Translator.translateToLocal("tile.electrolyzer.name")

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