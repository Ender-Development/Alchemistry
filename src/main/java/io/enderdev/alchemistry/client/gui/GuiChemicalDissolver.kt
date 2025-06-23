package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.container.ContainerChemicalDissolver
import io.enderdev.alchemistry.tiles.TileChemicalDissolver
import io.enderdev.catalyx.client.gui.BaseGui
import io.enderdev.catalyx.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

class GuiChemicalDissolver(playerInv: InventoryPlayer, tile: TileChemicalDissolver) : BaseGui(ContainerChemicalDissolver(playerInv, tile), tile) {
	override val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/chemical_dissolver_gui_redox.png")

	init {
		this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(63, 43, 175, 0, 32, 44)
	}
}
