package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.container.ContainerAtomizer
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityFluidDisplayWrapper
import io.enderdev.alchemistry.tiles.TileAtomizer
import net.minecraft.entity.player.InventoryPlayer


class GuiAtomizer(playerInv: InventoryPlayer, tile: TileAtomizer) :
	GuiBase<TileAtomizer>(ContainerAtomizer(playerInv, tile), tile, "atomizer") {

	init {
		this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
		this.displayData.add(CapabilityFluidDisplayWrapper(44, 21, 16, 70, tile::inputTank))
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(70, 75, 175, 0, 36, 16)
	}
}
