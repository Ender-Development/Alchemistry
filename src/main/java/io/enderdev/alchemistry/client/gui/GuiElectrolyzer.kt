package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.container.ContainerElectrolyzer
import io.enderdev.alchemistry.tiles.TileElectrolyzer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.client.gui.BaseGui
import org.ender_development.catalyx.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import org.ender_development.catalyx.client.gui.wrappers.CapabilityFluidDisplayWrapper

class GuiElectrolyzer(playerInv: InventoryPlayer, tile: TileElectrolyzer) : BaseGui(ContainerElectrolyzer(playerInv, tile), tile) {
	override val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/electrolyzer_gui_redox.png")

	init {
		this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
		this.displayData.add(CapabilityFluidDisplayWrapper(44, 21, 16, 70, tile::inputTank))
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(70, 56, 175, 0, 36, 36)
	}
}
