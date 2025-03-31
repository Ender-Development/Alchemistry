package io.enderdev.alchemistry.client.gui

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
		drawProgressBar(70, 75, 175, 0, 36, 16)
	}
}
