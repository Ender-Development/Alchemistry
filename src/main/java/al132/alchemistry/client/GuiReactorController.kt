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

abstract class GuiReactorController<T>(container: Container, tile: T, textureLocation: ResourceLocation) :
    GuiBase<T>(container, tile, textureLocation) where T : AbstractReactorController, T : IGuiTile {
    override var displayName: String = ""
    var textProductivity: String? = null
    var textSpeed: String? = null
    var textInvalid: String? = null

    var statusText: String = ""

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(7, 10, 16, 60, tile::energyStorage))
        when (tile.reactorType) {
            ReactorType.FISSION -> {
                displayName = Translator.translateToLocal("tile.fission_controller.name")
                textProductivity = "tile.fission.productivity"
                textSpeed = "tile.fission.speed"
                textInvalid = "tile.fission.invalid_multiblock"
            }
            ReactorType.FUSION -> {
                displayName = Translator.translateToLocal("tile.fusion_controller.name")
                textProductivity = "tile.fusion.productivity"
                textSpeed = "tile.fusion.speed"
                textInvalid = "tile.fusion.invalid_multiblock"
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        val productivity = tile.productivityModifier * 100
        val speed = tile.speedModifier * 100
        this.fontRenderer.drawString(
            I18n.format(textProductivity!!, "%.2f%%".format(productivity)),
            8.0f,
            115.0f,
            getColorFromValue(productivity),
            false
        )
        this.fontRenderer.drawString(
            I18n.format(textSpeed!!, "%.2f%%".format(speed)),
            8.0f,
            125.0f,
            getColorFromValue(speed),
            false
        )
        updateStatus()
    }

    private fun updateStatus() {
        statusText = if (tile.isMultiblockValid) {
            ""
        } else {
            Translator.translateToLocal(textInvalid.toString())
        }
    }

    private fun getColorFromValue(value: Double): Int {
        return when {
            value < 0 -> Color(148, 26, 26).rgb
            value > 0 -> Color(27, 105, 27).rgb
            else -> Color.GRAY.rgb
        }
    }

}