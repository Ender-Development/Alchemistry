package al132.alchemistry.client

import al132.alchemistry.tiles.AbstractReactorController
import al132.alchemistry.tiles.ReactorType
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.tiles.IGuiTile
import al132.alib.utils.Translator
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import java.awt.Color
import kotlin.math.ceil

abstract class GuiReactorController<T>(container: Container, tile: T, textureLocation: ResourceLocation) :
    GuiBase<T>(container, tile, textureLocation) where T : AbstractReactorController, T : IGuiTile {

    val infoHeight = 95.0f

    override var displayName: String = ""
    var textProductivity: String? = null
    var textSpeed: String? = null
    var textEnergy: String? = null
    var textInvalid: String? = null

    var statusText: String = ""

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile::energyStorage))
        when (tile.reactorType) {
            ReactorType.FISSION -> {
                displayName = Translator.translateToLocal("tile.fission_controller.name")
                textProductivity = "tile.fission.productivity"
                textSpeed = "tile.fission.speed"
                textEnergy = "tile.fission.energy"
                textInvalid = "tile.fission.invalid_multiblock"
            }

            ReactorType.FUSION -> {
                displayName = Translator.translateToLocal("tile.fusion_controller.name")
                textProductivity = "tile.fusion.productivity"
                textSpeed = "tile.fusion.speed"
                textEnergy = "tile.fusion.energy"
                textInvalid = "tile.fusion.invalid_multiblock"
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        val productivity = tile.productivityModifier * 100
        val speed = tile.speedModifier * 100
        val energy = tile.energyModifier * 100
        fontRenderer.drawString(
            statusText, ((xSize / 2 - fontRenderer.getStringWidth(statusText) / 2).toFloat()),
            infoHeight, Color.RED.rgb, false
        )
        fontRenderer.drawString(
            I18n.format(textProductivity!!, "%.2f%%".format(productivity)),
            8.0f,
            infoHeight + 10,
            getColorFromValue(productivity),
            false
        )
        fontRenderer.drawString(
            I18n.format(textSpeed!!, "%.2f%%".format(speed)),
            8.0f,
            infoHeight + 20,
            getColorFromValue(speed),
            false
        )
        fontRenderer.drawString(
            I18n.format(textEnergy!!, "%.2f%%".format(energy)),
            8.0f,
            infoHeight + 30,
            getColorFromValue(energy, invert = true),
            false
        )
        updateStatus()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        drawModifierText(mouseX, mouseY)
    }

    private fun updateStatus() {
        statusText = if (tile.isMultiblockValid) {
            ""
        } else {
            Translator.translateToLocal(textInvalid.toString())
        }
    }

    private fun getColorFromValue(value: Double, invert: Boolean = false): Int {
        return when {
            !invert && value > 0 -> Color(27, 105, 27).rgb
            !invert && value < 0 -> Color(148, 26, 26).rgb
            invert && value > 0 -> Color(148, 26, 26).rgb
            invert && value < 0 -> Color(27, 105, 27).rgb
            else -> Color.GRAY.rgb
        }
    }

    private fun drawModifierText(mouseX: Int, mouseY: Int) {
        if (mouseX < guiLeft + 8 || mouseX > guiLeft + xSize - 8 || mouseY < guiTop || mouseY > guiTop + ySize) return
        val fontHeight = fontRenderer.FONT_HEIGHT
        val y = (this.height - this.ySize) / 2
        val offset = y + infoHeight
        when {
            offset + 10 <= mouseY && mouseY <= offset + 10 + fontHeight -> {
                drawHoveringText(
                    listOf(
                        I18n.format("tooltip.productivity.title"),
                        I18n.format("tooltip.productivity.default"),
                        I18n.format(
                            "tooltip.productivity.current",
                            if (ceil(tile.productivityModifier) == 0.0) 1 else ceil(tile.productivityModifier).toInt()
                        )
                    ),
                    mouseX,
                    mouseY
                )
            }

            offset + 20 <= mouseY && mouseY <= offset + 20 + fontHeight -> {
                drawHoveringText(
                    listOf(
                        I18n.format("tooltip.speed.title"),
                        I18n.format("tooltip.speed.default", tile.defaultProcessTime),
                        I18n.format("tooltip.speed.current", tile.getModifiedProcessTime())
                    ),
                    mouseX,
                    mouseY
                )
            }

            offset + 30 <= mouseY && mouseY <= offset + 30 + fontHeight -> {
                drawHoveringText(
                    listOf(
                        I18n.format("tooltip.energy.title"),
                        I18n.format("tooltip.energy.default", tile.defaultEnergyPerTick),
                        I18n.format("tooltip.energy.current", tile.getModifiedEnergyCost())
                    ),
                    mouseX,
                    mouseY
                )
            }
        }
    }
}