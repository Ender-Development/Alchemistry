package al132.alchemistry.client

import al132.alchemistry.ConfigHandler
import al132.alchemistry.tiles.TileFissionController
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.utils.Translator
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * Created by al132 on 1/16/2017.
 */
class GuiFissionController(playerInv: InventoryPlayer, tile: TileFissionController) :
        GuiBase<TileFissionController>(ContainerFissionController(playerInv, tile), tile, GuiFissionController.textureLocation) {


    companion object {
        val textureLocation = ResourceLocation(root + "fission_controller_gui.png")
    }

    var statusText: String = ""

    override val displayName = Translator.translateToLocal("tile.fission_controller.name")

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile::energyStorage))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(28, tile.progressTicks, tile.getModifiedProcessTime(ConfigHandler.FISSION.processingTicks))
            this.drawTexturedModalRect(i + 79, j + 63, 175, 0, k, 9)
        }
    }

    fun updateStatus() {
        if (tile.isMultiblockValid) statusText = ""
        else statusText = Translator.translateToLocal("tile.fission.invalid_multiblock")
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        updateStatus()
        this.fontRenderer.drawStringWithShadow(statusText, 30.0f, 100.0f, Color.WHITE.rgb)
        // upgrades
        this.fontRenderer.drawStringWithShadow(I18n.format("tile.fission.productivity", "%.2f%%".format(tile.productivityModifier * 100)), 8.0f, 120.0f, Color.WHITE.rgb)
        this.fontRenderer.drawStringWithShadow(I18n.format("tile.fission.speed", "%.2f%%".format(tile.speedModifier * 100)), 8.0f, 130.0f, Color.WHITE.rgb)
    }
}