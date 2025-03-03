package al132.alchemistry.client


import al132.alib.client.ALGuiBase
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.tiles.ALTile
import al132.alib.tiles.IGuiTile
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import java.awt.Color


abstract class GuiBase<T>(container: Container, tile: T, textureLocation: ResourceLocation) :
    ALGuiBase<T>(container, tile, textureLocation) where T : ALTile, T : IGuiTile {

    override var powerBarTexture: ResourceLocation? = ResourceLocation(root + "template.png")

    companion object {
        val root = "alchemistry:textures/gui/container/"
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawPowerBar(
        storage: CapabilityEnergyDisplayWrapper,
        texture: ResourceLocation,
        textureX: Int,
        textureY: Int
    ) {
        val i = storage.x + ((this.width - this.xSize) / 2)
        val j = storage.y + ((this.height - this.ySize) / 2)
        val k = this.getBarScaled(storage.height, storage.getStored(), storage.getCapacity())
        mc.textureManager.bindTexture(texture)
        this.drawTexturedModalRect(i, j, textureX, textureY, storage.width, storage.height)
        if (storage.getStored() > 0) {
            this.drawTexturedModalRect(i, j + storage.height - k, textureX + 38, textureY, storage.width, k)
        }
        this.mc.textureManager.bindTexture(this.textureLocation)
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        if (this.displayName.isNotEmpty()) {
            this.fontRenderer.drawString(this.displayName,
                    this.xSize / 2 - this.fontRenderer.getStringWidth(this.displayName) / 2, 10, Color.DARK_GRAY.rgb)
        }
    }
}