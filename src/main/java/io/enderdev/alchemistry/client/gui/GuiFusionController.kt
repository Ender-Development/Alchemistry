package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.button.SingleButton
import io.enderdev.alchemistry.client.container.ContainerFusionController
import io.enderdev.alchemistry.network.ButtonPacket
import io.enderdev.alchemistry.network.PacketHandler
import io.enderdev.alchemistry.tiles.TileFusionController
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

    lateinit var modeButton: SingleButton

    override fun initGui() {
        super.initGui()
        modeButton = SingleButton(this.guiLeft + 175 - 20, this.guiTop + displayNameOffset - 4 + 18)
        this.buttonList.add(modeButton)
    }

    override fun actionPerformed(guibutton: GuiButton) {
        super.actionPerformed(guibutton)
        if (guibutton.id == modeButton.id) {
            PacketHandler.INSTANCE!!.sendToServer(ButtonPacket(tile.pos, single = true))
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(36, tile.progressTicks, tile.recipeTime)
            this.drawTexturedModalRect(i + 88, j + 75, 175, 0, k, 16)
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        if (tile.singleMode) modeButton.isSingle = SingleButton.State.SINGLE
        else modeButton.isSingle = SingleButton.State.REGULAR
    }

    override fun renderTooltips(mouseX: Int, mouseY: Int) {
        super.renderTooltips(mouseX, mouseY)
        if (isHovered(modeButton.x, modeButton.y, 16, 16, mouseX, mouseY)) {
            if (tile.singleMode)
                this.drawHoveringText(listOf(
                    Translator.translateToLocal("tooltip.single"),
                    Translator.translateToLocal("tooltip.single.1"),
                    Translator.translateToLocal("tooltip.single.2")
                ), mouseX, mouseY)
            else
                this.drawHoveringText(listOf(
                    Translator.translateToLocal("tooltip.regular"),
                    Translator.translateToLocal("tooltip.regular.1"),
                    Translator.translateToLocal("tooltip.regular.2")
                ), mouseX, mouseY)
        }
    }
}