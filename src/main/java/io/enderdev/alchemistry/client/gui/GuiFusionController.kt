package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.button.SingleButton
import io.enderdev.alchemistry.client.container.ContainerFusionController
import io.enderdev.alchemistry.network.ButtonPacket
import io.enderdev.alchemistry.network.PacketHandler
import io.enderdev.alchemistry.tiles.TileFusionController
import io.enderdev.alchemistry.utils.extensions.translate
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.InventoryPlayer


class GuiFusionController(playerInv: InventoryPlayer, tile: TileFusionController) :
	GuiReactorController<TileFusionController>(ContainerFusionController(playerInv, tile), tile, "fusion_controller") {

	lateinit var modeButton: SingleButton

	override fun initGui() {
		super.initGui()
		modeButton = SingleButton(this.guiLeft + 175 - 20, this.guiTop + displayNameOffset - 4 + 18)
		this.buttonList.add(modeButton)
	}

	override fun actionPerformed(guibutton: GuiButton) {
		super.actionPerformed(guibutton)
		if(guibutton.id == modeButton.id) {
			PacketHandler.INSTANCE!!.sendToServer(ButtonPacket(tile.pos, single = true))
		}
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(88, 75, 175, 0, 36, 16)
	}

	override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY)
		if(tile.singleMode) modeButton.isSingle = SingleButton.State.SINGLE
		else modeButton.isSingle = SingleButton.State.REGULAR
	}

	override fun renderTooltips(mouseX: Int, mouseY: Int) {
		super.renderTooltips(mouseX, mouseY)
		if(isHovered(modeButton.x, modeButton.y, 16, 16, mouseX, mouseY)) {
			if(tile.singleMode)
				this.drawHoveringText(
					listOf(
						"tooltip.single".translate(),
						"tooltip.single.1".translate(),
						"tooltip.single.2".translate()
					), mouseX, mouseY
				)
			else
				this.drawHoveringText(
					listOf(
						"tooltip.regular".translate(),
						"tooltip.regular.1".translate(),
						"tooltip.regular.2".translate()
					), mouseX, mouseY
				)
		}
	}
}
