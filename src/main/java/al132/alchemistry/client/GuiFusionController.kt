package al132.alchemistry.client

import al132.alchemistry.ConfigHandler
import al132.alchemistry.network.FusionModePacket
import al132.alchemistry.network.PacketHandler
import al132.alchemistry.tiles.TileFusionController
import al132.alib.utils.Translator
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiFusionController(playerInv: InventoryPlayer, tile: TileFusionController) :
        GuiReactorController<TileFusionController>(ContainerFusionController(playerInv, tile), tile, textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "fusion_controller_gui.png")
    }

    lateinit var modeButton: GuiButton

    override fun initGui() {
        super.initGui()
        modeButton = GuiButton(0, this.guiLeft + 150 - 80, this.guiTop + 25, 80, 20, "Test")
        this.buttonList.add(modeButton)
    }

    override fun actionPerformed(guibutton: GuiButton) {
        when (guibutton.id) {
            modeButton.id -> PacketHandler.INSTANCE!!.sendToServer(FusionModePacket(tile.pos, singleMode = true))
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(36, tile.progressTicks, tile.getModifiedProcessTime())
            this.drawTexturedModalRect(i + 88, j + 75, 175, 0, k, 16)
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        updateButtonStrings()
    }

    private fun updateButtonStrings() {
        if (tile.singleMode) modeButton.displayString = Translator.translateToLocal("tile.fusion.single_mode")
        else modeButton.displayString = Translator.translateToLocal("tile.fusion.regular_mode")
    }
}