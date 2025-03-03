package al132.alchemistry.client

import al132.alchemistry.tiles.TileChemicalDissolver
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.utils.Translator
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation

/**
 * Created by al132 on 1/16/2017.
 */
class GuiChemicalDissolver(playerInv: InventoryPlayer, tile: TileChemicalDissolver) :
        GuiBase<TileChemicalDissolver>(ContainerChemicalDissolver(playerInv, tile), tile, textureLocation) {

    companion object {
        val textureLocation = ResourceLocation(root + "chemical_dissolver_gui.png")
    }

    override val displayName = Translator.translateToLocal("tile.chemical_dissolver.name")

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 10, 16, 60, tile::energyStorage))
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.canProcess()) {
            this.drawTexturedModalRect(i + 69, j + 21, 175, 0, 38, 80)
        }
    }
}