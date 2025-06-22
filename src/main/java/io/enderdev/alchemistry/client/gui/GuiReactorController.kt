package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.button.ModeratorButton
import io.enderdev.alchemistry.client.gui.misc.GuiModifiers
import io.enderdev.alchemistry.client.gui.misc.GuiModifiers.IRenderer
import io.enderdev.alchemistry.tiles.AbstractReactorController
import io.enderdev.alchemistry.tiles.ReactorType
import io.enderdev.catalyx.utils.extensions.translate
import io.enderdev.catalyx.client.gui.BaseGui
import io.enderdev.catalyx.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import io.enderdev.catalyx.tiles.helper.IGuiTile
import net.minecraft.client.gui.GuiButton
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

abstract class GuiReactorController<T>(container: Container, tile: T, guiName: String) :
	BaseGui<T>(container, tile, guiName) where T : AbstractReactorController<*>, T : IGuiTile {
	override val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/${guiName}_gui_redox.png")

	val infoHeight = 102f
	val infoX = 12f

	val hasModerators = tile.moderators.isNotEmpty()
	lateinit var moderatorButton: ModeratorButton

	init {
		displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
	}

	override fun initGui() {
		super.initGui()
		moderatorButton = ModeratorButton(guiLeft + 155, guiTop + displayNameOffset + 14)
		if(hasModerators)
			buttonList.add(moderatorButton)
	}

	override fun actionPerformed(button: GuiButton) {
		super.actionPerformed(button)
		if(button.id == moderatorButton.id) {
			// this really is not pretty, but, what can you do
			val entries = mutableListOf<Pair<IRenderer, List<Pair<String, Color>>>>()
			val sort = { by: (AbstractReactorController.Multiplier) -> Double ->
				{ reverse: Boolean ->
					entries.clear()
					entries.addAll(tile.moderators.entries.sortedBy {
						by(it.value) * (if(reverse) -1 else 1)
					}.map { (block, multiplier) ->
						block.getGUIRenderer(this) to arrayOf(multiplier.productivity, multiplier.processingTime, multiplier.energy).mapIndexed { idx, it ->
							"${if(it < 0) "" else '+'}${Alchemistry.DECIMAL_FORMAT.format(it)}x" to Color(getColorFromValue(it + 1, idx != 0))
						}
					})
					Unit
				}
			}
			sort { .0 }(false)
			val gui = GuiModifiers(
				"${tile.reactorType.name.lowercase(Locale.getDefault()).replaceFirstChar(Char::uppercaseChar)} ${"tile.alchemistry:reactor.modifers".translate()}",
				entries,
				arrayOf("output_multiplier", "processing_time", "energy_consumption").map { "tile.alchemistry:reactor.$it.short".translate("") }.toTypedArray(),
				arrayOf(sort { it.productivity }, sort { it.processingTime }, sort { it.energy }),
				this
			)
			mc.displayGuiScreen(gui)
		}
	}

	override fun renderTooltips(mouseX: Int, mouseY: Int) {
		super.renderTooltips(mouseX, mouseY)
		if(hasModerators && isHovered(moderatorButton.x, moderatorButton.y, 16, 16, mouseX, mouseY))
			drawHoveringText("tooltip.modifier_btn".translate(), mouseX, mouseY)
	}

	override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY)
		if(tile.isMultiblockValid) {
			val (productivity, processingTime, energy) = tile.currentMultiplier
			fontRenderer.drawString(
				"tile.alchemistry:reactor.output_multiplier".translate("${Alchemistry.DECIMAL_FORMAT.format(productivity)}x"),
				infoX,
				infoHeight,
				getColorFromValue(productivity),
				false
			)
			fontRenderer.drawString(
				"tile.alchemistry:reactor.processing_time".translate("${Alchemistry.DECIMAL_FORMAT.format(processingTime)}x"),
				infoX,
				infoHeight + 10,
				getColorFromValue(processingTime, true),
				false
			)
			fontRenderer.drawString(
				"tile.alchemistry:reactor.energy_consumption".translate("${Alchemistry.DECIMAL_FORMAT.format(energy)}x"),
				infoX,
				infoHeight + 20,
				getColorFromValue(energy, true),
				false
			)
		} else {
			val reason = tile.shapeHandler.failReason()
			if(reason == null || reason.isEmpty())
				return
			reason.forEachIndexed { idx, line ->
				fontRenderer.drawString(
					line,
					(xSize - fontRenderer.getStringWidth(line)) / 2f,
					infoHeight + 10 * idx,
					Color(170, 0, 0).rgb,
					false
				)
			}
			if(tile.shapeHandler.failPos != null)
				tile.shapeHandler.highlightIncorrect()
		}
	}

	override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
		super.drawScreen(mouseX, mouseY, partialTicks)
		drawModifierText(mouseX, mouseY)
	}

	private fun drawModifierText(mouseX: Int, mouseY: Int) {
		if(mouseX < guiLeft + infoX || mouseX > guiLeft + xSize - infoX - 12 || mouseY < guiTop || mouseY > guiTop + ySize || !tile.isMultiblockValid) return
		val fontHeight = fontRenderer.FONT_HEIGHT
		val y = (this.height - this.ySize) / 2
		val offset = y + infoHeight
		when {
			offset <= mouseY && mouseY <= offset + fontHeight -> {
				val (productivity) = tile.currentMultiplier
				val textLines = mutableListOf(
					"tooltip.output_multiplier.title".translate(),
					"tooltip.output_multiplier.default".translate(),
					"tooltip.output_multiplier.current".translate(ceil(productivity).toInt())
				)
				if(productivity <= 0)
					textLines.add("tooltip.output_multiplier.explanation.no_output".translate())
				else if(productivity < 1)
					textLines.add("tooltip.output_multiplier.explanation.probability".translate("${(productivity * 100).roundToInt()}%"))
				else {
					val extraPercent = ((productivity - productivity.toInt()) * 100).roundToInt()
					textLines.add("tooltip.output_multiplier.explanation".translate(productivity.toInt()) + if(extraPercent == 0) "" else ",")
					if(extraPercent != 0)
						textLines.add("tooltip.output_multiplier.explanation.2".translate(extraPercent, ceil(productivity).toInt()))
				}
				drawHoveringText(textLines, mouseX, mouseY)
			}

			offset + 10 <= mouseY && mouseY <= offset + 10 + fontHeight -> {
				val defaultTime = if(tile.reactorType == ReactorType.FISSION) {
					ConfigHandler.FISSION.processingTicks
				} else {
					ConfigHandler.FUSION.processingTicks
				}
				drawHoveringText(
					listOf(
						"tooltip.processing_time.title".translate(),
						"tooltip.processing_time.default".translate(defaultTime),
						"tooltip.processing_time.current".translate(tile.recipeTime)
					),
					mouseX,
					mouseY
				)
			}

			offset + 20 <= mouseY && mouseY <= offset + 20 + fontHeight -> {
				val defaultEnergy = if(tile.reactorType == ReactorType.FISSION) {
					ConfigHandler.FISSION.energyPerTick
				} else {
					ConfigHandler.FUSION.energyPerTick
				}
				val minimum = if(tile.reactorType == ReactorType.FISSION)
					ConfigHandler.FISSION.minEnergyPerTick
				else
					ConfigHandler.FUSION.minEnergyPerTick
				drawHoveringText(
					listOf(
						"tooltip.energy_consumption.title".translate(),
						"tooltip.energy_consumption.default".translate(defaultEnergy),
						"tooltip.energy_consumption.current".translate(tile.energyPerTick),
						"tooltip.energy_consumption.minimum".translate(minimum)
					),
					mouseX,
					mouseY
				)
			}
		}
	}

	companion object {
		fun getColorFromValue(value: Double, invert: Boolean = false): Int {
			val green = Color(27, 155, 27).rgb
			val red = Color(198, 26, 26).rgb
			return when {
				!invert && value > 1 -> green
				!invert && value < 1 -> red
				invert && value > 1 -> red
				invert && value < 1 -> green
				else -> Color.GRAY.rgb
			}
		}
	}
}
