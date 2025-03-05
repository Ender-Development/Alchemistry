package al132.alchemistry.client

import al132.alchemistry.tiles.AbstractReactorController
import al132.alchemistry.tiles.ReactorType
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.tiles.IGuiTile
import al132.alib.utils.Translator
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import java.awt.Color
import kotlin.math.ceil

abstract class GuiReactorController<T>(container: Container, tile: T, textureLocation: ResourceLocation) :
    GuiBase<T>(container, tile, textureLocation) where T : AbstractReactorController, T : IGuiTile {

    val infoHeight = 102.0f
    val infoX = 12.0f

    override val displayNameOffset: Int = 8
    override var displayName: String = ""
    var textProductivity: String? = null
    var textSpeed: String? = null
    var textEnergy: String? = null
    var textInvalid: String? = null
    var textValid: String? = null

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
        when (tile.reactorType) {
            ReactorType.FISSION -> {
                displayName = Translator.translateToLocal("tile.fission_controller.name")
                textProductivity = "tile.fission.productivity"
                textSpeed = "tile.fission.speed"
                textEnergy = "tile.fission.energy"
                textInvalid = "tile.fission.invalid_multiblock"
                textValid = "tile.fission.valid_multiblock"
            }

            ReactorType.FUSION -> {
                displayName = Translator.translateToLocal("tile.fusion_controller.name")
                textProductivity = "tile.fusion.productivity"
                textSpeed = "tile.fusion.speed"
                textEnergy = "tile.fusion.energy"
                textInvalid = "tile.fusion.invalid_multiblock"
                textValid = "tile.fusion.valid_multiblock"
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        val productivity = tile.productivityModifier * 100
        val speed = tile.speedModifier * 100
        val energy = tile.energyModifier * 100
        if (tile.isMultiblockValid) {
            fontRenderer.drawString(
                I18n.format(textProductivity!!, "%.2f%%".format(productivity)),
                infoX,
                infoHeight,
                getColorFromValue(productivity),
                false
            )
            fontRenderer.drawString(
                I18n.format(textSpeed!!, "%.2f%%".format(speed)),
                infoX,
                infoHeight + 10,
                getColorFromValue(speed),
                false
            )
            fontRenderer.drawString(
                I18n.format(textEnergy!!, "%.2f%%".format(energy)),
                infoX,
                infoHeight + 20,
                getColorFromValue(energy, invert = true),
                false
            )
        } else {
            val invalid = Translator.translateToLocal(textInvalid!!)
            fontRenderer.drawString(
                invalid, ((xSize / 2 - fontRenderer.getStringWidth(invalid) / 2).toFloat()),
                infoHeight + 12, Color(170, 0, 0).rgb, false
            )
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        drawModifierText(mouseX, mouseY)
    }

    private fun getColorFromValue(value: Double, invert: Boolean = false): Int {
        return when {
            !invert && value > 0 -> Color(27, 155, 27).rgb
            !invert && value < 0 -> Color(198, 26, 26).rgb
            invert && value > 0 -> Color(198, 26, 26).rgb
            invert && value < 0 -> Color(27, 155, 27).rgb
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

            offset + 10 <= mouseY && mouseY <= offset + 10 + fontHeight -> {
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

            offset + 20 <= mouseY && mouseY <= offset + 20 + fontHeight -> {
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