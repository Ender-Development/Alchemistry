package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.container.ContainerEvaporator
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityFluidDisplayWrapper
import io.enderdev.alchemistry.tiles.TileEvaporator
import net.minecraft.entity.player.InventoryPlayer

/**
 * Created by al132 on 4/29/2017.
 */

class GuiEvaporator(playerInv: InventoryPlayer, tile: TileEvaporator) : GuiBase<TileEvaporator>(ContainerEvaporator(playerInv, tile), tile, "evaporator") {

	init {
		this.displayData.add(CapabilityFluidDisplayWrapper(44, 21, 16, 70, tile::inputTank))
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(70, 75, 175, 0, 36, 16)
	}
}
