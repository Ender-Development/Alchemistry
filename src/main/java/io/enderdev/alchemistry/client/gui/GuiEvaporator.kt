package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.container.ContainerEvaporator
import io.enderdev.alchemistry.client.gui.misc.GuiModifiers
import io.enderdev.alchemistry.client.gui.misc.GuiModifiers.MouseClickData
import io.enderdev.alchemistry.tiles.TileEvaporator
import io.enderdev.catalyx.client.gui.BaseGuiTyped
import io.enderdev.catalyx.client.gui.wrappers.CapabilityFluidDisplayWrapper
import io.enderdev.catalyx.utils.extensions.translate
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import java.awt.Color
import kotlin.math.roundToInt

class GuiEvaporator(playerInv: InventoryPlayer, val tile: TileEvaporator) : BaseGuiTyped.BaseGui(ContainerEvaporator(playerInv, tile), tile) {
	override val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/evaporator_gui_redox.png")
	private val hasHeatSources = TileEvaporator.heatSources.isNotEmpty()
	private val heatText = { heat: Double -> "tile.alchemistry:evaporator.heat".translate(Alchemistry.DECIMAL_FORMAT.format(heat)) }
	private var mouseClick: MouseClickData? = null

	init {
		this.displayData.add(CapabilityFluidDisplayWrapper(44, 21, 16, 70, tile::inputTank))
	}

	override fun renderTooltips(mouseX: Int, mouseY: Int) {
		super.renderTooltips(mouseX, mouseY)
		val width = fontRenderer.getStringWidth(heatText(tile.getHeat()))
		if(isHovered(guiLeft + 65, guiTop + 64, width, fontRenderer.FONT_HEIGHT, mouseX, mouseY)) {
			val tooltipLines = mutableListOf(1, 2)
			if(hasHeatSources)
				tooltipLines.add(3)
			drawHoveringText(tooltipLines.map { "tile.alchemistry:evaporator.heat_explanation.$it".translate() }, mouseX, mouseY)
		}
		if(mouseClick != null && mouseClick!!.btn == 0 && isHovered(guiLeft + 65, guiTop + 64, width, fontRenderer.FONT_HEIGHT, mouseClick!!.x, mouseClick!!.y)) {
			val gui = GuiModifiers(
				"$displayName ${"tile.alchemistry:evaporator.heat_sources".translate()}",
				TileEvaporator.heatSources.map { (block, value) ->
					block.getGUIRenderer(this) to listOf("${Alchemistry.DECIMAL_FORMAT.format(value)}x" to getColor(value))
				},
				arrayOf("tile.alchemistry:evaporator.heat".translate("")),
				emptyArray(),
				this
			)
			mc.displayGuiScreen(gui)
		}
		mouseClick = null
	}

	override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY)
		val heat = tile.getHeat()
		fontRenderer.drawString(heatText(heat), 65, 64, getColor(heat).rgb)
	}

	fun getColor(heat: Double): Color {
		val r = (Color.DARK_GRAY.red * heat).roundToInt().coerceIn(0, 255)
		val b = (Color.DARK_GRAY.blue / heat).roundToInt().coerceIn(0, 255)
		return Color(r, Color.DARK_GRAY.green, b)
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(70, 75, 175, 0, 36, 16)
	}

	override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
		super.mouseClicked(mouseX, mouseY, mouseButton)
		mouseClick = MouseClickData(mouseX, mouseY, mouseButton)
	}
}
