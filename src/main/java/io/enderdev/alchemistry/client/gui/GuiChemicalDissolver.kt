package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.client.container.ContainerChemicalDissolver
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import io.enderdev.alchemistry.tiles.TileChemicalDissolver
import net.minecraft.entity.player.InventoryPlayer

/**
 * Created by al132 on 1/16/2017.
 */
class GuiChemicalDissolver(playerInv: InventoryPlayer, tile: TileChemicalDissolver) :
	GuiBase<TileChemicalDissolver>(ContainerChemicalDissolver(playerInv, tile), tile, "chemical_dissolver") {

	init {
		this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(63, 43, 175, 0, 32, 44)
	}
}
