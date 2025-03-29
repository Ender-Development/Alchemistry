package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import io.enderdev.alchemistry.tiles.AbstractReactorController
import io.enderdev.alchemistry.tiles.ReactorType
import io.enderdev.alchemistry.tiles.tags.IGuiTile
import io.enderdev.alchemistry.utils.extensions.translate
import net.minecraft.inventory.Container
import java.awt.Color
import kotlin.math.ceil

abstract class GuiReactorController<T>(container: Container, tile: T, guiName: String) :
    GuiBase<T>(container, tile, guiName) where T : AbstractReactorController<*>, T : IGuiTile {

    val infoHeight = 102f
    val infoX = 12f

    val textProductivity: String
    val textSpeed: String
    val textEnergy: String
    val textInvalid: String
    val textValid: String

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
        val type = if(tile.reactorType == ReactorType.FISSION) "fission" else "fusion"
        textProductivity = "tile.$type.productivity"
        textSpeed = "tile.$type.speed"
        textEnergy = "tile.$type.energy"
        textInvalid = "tile.$type.invalid_multiblock"
        textValid = "tile.$type.valid_multiblock"
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        val productivity = tile.productivityModifier * 100
        val speed = tile.speedModifier * 100
        val energy = tile.energyModifier * 100
        if (tile.isMultiblockValid) {
            fontRenderer.drawString(
                textProductivity.translate("%.2f%%".format(productivity)),
                infoX,
                infoHeight,
                getColorFromValue(productivity),
                false
            )
            fontRenderer.drawString(
                textSpeed.translate("%.2f%%".format(speed)),
                infoX,
                infoHeight + 10,
                getColorFromValue(speed),
                false
            )
            fontRenderer.drawString(
                textEnergy.translate("%.2f%%".format(energy)),
                infoX,
                infoHeight + 20,
                getColorFromValue(energy, invert = true),
                false
            )
        } else {
            val (text, coords) = tile.shapeHandler.failReason()!!
            fontRenderer.drawString(
                text,
                (xSize - fontRenderer.getStringWidth(text)) / 2f,
                infoHeight,
                Color(170, 0, 0).rgb,
                false
            )
            fontRenderer.drawString(
                coords,
                (xSize - fontRenderer.getStringWidth(coords)) / 2f,
                infoHeight + 10,
                Color(170, 0, 0).rgb,
                false
            )
            if(tile.shapeHandler.failPos != null)
                tile.shapeHandler.highlightIncorrect()
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        drawModifierText(mouseX, mouseY)
    }

    private fun getColorFromValue(value: Double, invert: Boolean = false): Int {
        val green = Color(27, 155, 27).rgb
        val red = Color(198, 26, 26).rgb
        return when {
            !invert && value > 0 -> green
            !invert && value < 0 -> red
            invert && value > 0 -> red
            invert && value < 0 -> green
            else -> Color.GRAY.rgb
        }
    }

    private fun drawModifierText(mouseX: Int, mouseY: Int) {
        if (mouseX < guiLeft + infoX || mouseX > guiLeft + xSize - infoX || mouseY < guiTop || mouseY > guiTop + ySize || !tile.isMultiblockValid) return
        val fontHeight = fontRenderer.FONT_HEIGHT
        val y = (this.height - this.ySize) / 2
        val offset = y + infoHeight
        when {
            offset <= mouseY && mouseY <= offset + fontHeight -> {
                drawHoveringText(
                    listOf(
                        "tooltip.productivity.title".translate(),
                        "tooltip.productivity.default".translate(),
                        "tooltip.productivity.current".translate(
                            if (ceil(tile.productivityModifier) == 0.0) 1 else ceil(tile.productivityModifier).toInt()
                        )
                    ),
                    mouseX,
                    mouseY
                )
            }

            offset + 10 <= mouseY && mouseY <= offset + 10 + fontHeight -> {
                val defaultTime = if (tile.reactorType == ReactorType.FISSION) {
                    ConfigHandler.FISSION.processingTicks
                } else {
                    ConfigHandler.FUSION.processingTicks
                }
                drawHoveringText(
                    listOf(
                        "tooltip.speed.title".translate(),
                        "tooltip.speed.default".translate(defaultTime),
                        "tooltip.speed.current".translate(tile.recipeTime)
                    ),
                    mouseX,
                    mouseY
                )
            }

            offset + 20 <= mouseY && mouseY <= offset + 20 + fontHeight -> {
                val defaultEnergy = if (tile.reactorType == ReactorType.FISSION) {
                    ConfigHandler.FISSION.energyPerTick
                } else {
                    ConfigHandler.FUSION.energyPerTick
                }
                drawHoveringText(
                    listOf(
                        "tooltip.energy.title".translate(),
                        "tooltip.energy.default".translate(defaultEnergy),
                        "tooltip.energy.current".translate(tile.energyPerTick)
                    ),
                    mouseX,
                    mouseY
                )
            }
        }
    }
}