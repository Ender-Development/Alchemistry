package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.button.SingleButtonWrapper
import io.enderdev.alchemistry.client.container.ContainerFusionController
import io.enderdev.alchemistry.tiles.TileFusionController
import io.enderdev.catalyx.utils.extensions.translate
import net.minecraft.entity.player.InventoryPlayer

class GuiFusionController(playerInv: InventoryPlayer, tile: TileFusionController) : GuiReactorController<TileFusionController>(ContainerFusionController(playerInv, tile), tile, "fusion_controller") {
	lateinit var modeButton: SingleButtonWrapper

	override fun initGui() {
		super.initGui()
		modeButton = SingleButtonWrapper(this.guiLeft + 137, this.guiTop + displayNameOffset + 14)
		this.buttonList.add(modeButton.button)
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(88, 75, 175, 0, 36, 16)
	}

	override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY)
		if(tile.singleMode) modeButton.isSingle = SingleButtonWrapper.State.SINGLE
		else modeButton.isSingle = SingleButtonWrapper.State.REGULAR
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
