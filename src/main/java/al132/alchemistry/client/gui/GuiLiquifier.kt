package al132.alchemistry.client.gui

import al132.alchemistry.ConfigHandler
import al132.alchemistry.client.container.ContainerLiquifier
import al132.alchemistry.tiles.TileLiquifier
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.CapabilityFluidDisplayWrapper
import al132.alib.utils.Translator
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiLiquifier(playerInv: InventoryPlayer, tile: TileLiquifier, override val displayNameOffset: Int = 51) :
        GuiBase<TileLiquifier>(ContainerLiquifier(playerInv, tile), tile, textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "liquifier_gui.png")
    }

    override val displayName = Translator.translateToLocal("tile.liquifier.name")

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 64, 16, 70, tile::energyStorage))
        this.displayData.add(CapabilityFluidDisplayWrapper(116, 64, 16, 70, tile::outputTank))
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