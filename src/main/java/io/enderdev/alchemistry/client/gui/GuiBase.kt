package io.enderdev.alchemistry.client.gui


import al132.alib.client.ALGuiBase
import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.client.CapabilityFluidDisplayWrapper
import al132.alib.tiles.IGuiTile
import al132.alib.utils.Translator
import al132.alib.utils.extensions.translate
import io.enderdev.alchemistry.Reference
import io.enderdev.alchemistry.client.button.PauseButton
import io.enderdev.alchemistry.client.button.RedstoneButton
import io.enderdev.alchemistry.network.ButtonPacket
import io.enderdev.alchemistry.network.PacketHandler
import io.enderdev.alchemistry.tiles.AbstractMachine
import net.minecraft.client.gui.GuiButton
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import java.awt.Color


abstract class GuiBase<T>(container: Container, tile: T, guiName: String) :
    ALGuiBase<T>(container, tile, ResourceLocation(Reference.MODID, "textures/gui/container/${guiName}_gui_redox.png")) where T : AbstractMachine<*>, T : IGuiTile {

    abstract val displayNameOffset: Int

    override var powerBarTexture: ResourceLocation? = ResourceLocation(Reference.MODID, "textures/gui/container/template_redox.png")
    override val displayName = "tile.$guiName.name".translate()

    lateinit var pauseButton: PauseButton
    lateinit var redstoneButton: RedstoneButton

    override fun initGui() {
        super.initGui()
        pauseButton = PauseButton(this.guiLeft + 175 - 20, this.guiTop + displayNameOffset - 4)
        this.buttonList.add(pauseButton)
        redstoneButton = RedstoneButton(this.guiLeft + 175 - 38, this.guiTop + displayNameOffset - 4)
        this.buttonList.add(redstoneButton)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
        this.renderTooltips(mouseX, mouseY)
    }

    open fun renderTooltips(mouseX: Int, mouseY: Int) {
        if (isHovered(pauseButton.x, pauseButton.y, 16, 16, mouseX, mouseY)) {
            if (tile.isPaused)
                this.drawHoveringText(listOf(Translator.translateToLocal("tooltip.paused")), mouseX, mouseY)
            else
                this.drawHoveringText(listOf(Translator.translateToLocal("tooltip.running")), mouseX, mouseY)
        }
        if (isHovered(redstoneButton.x, redstoneButton.y, 16, 16, mouseX, mouseY)) {
            if (tile.needsPower)
                this.drawHoveringText(listOf(Translator.translateToLocal("tooltip.redstone_high")), mouseX, mouseY)
            else
                this.drawHoveringText(listOf(Translator.translateToLocal("tooltip.redstone_low")), mouseX, mouseY)
        }
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
            this.drawTexturedModalRect(i, j + storage.height - k, textureX + 16, textureY, storage.width, k)
        }
        this.mc.textureManager.bindTexture(this.textureLocation)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == pauseButton.id) {
            PacketHandler.INSTANCE!!.sendToServer(ButtonPacket(tile.pos, pause = true))
        }
        if (button.id == redstoneButton.id) {
            PacketHandler.INSTANCE!!.sendToServer(ButtonPacket(tile.pos, redstone = true))
        }
    }

    override fun drawFluidTank(wrapper: CapabilityFluidDisplayWrapper, i: Int, j: Int, width: Int, height: Int) {
        super.drawFluidTank(wrapper, i, j, width = 16, height = 70)
        val i = wrapper.x + ((this.width - this.xSize) / 2)
        val j = wrapper.y + ((this.height - this.ySize) / 2)
        mc.textureManager.bindTexture(powerBarTexture!!)
        this.drawTexturedModalRect(i, j, 32, 0, 16, 70)
        this.mc.textureManager.bindTexture(this.textureLocation)
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        if (tile.isPaused) pauseButton.isPaused = PauseButton.State.PAUSED
        else pauseButton.isPaused = PauseButton.State.RUNNING

        if (tile.needsPower) redstoneButton.needsPower = RedstoneButton.State.ON
        else redstoneButton.needsPower = RedstoneButton.State.OFF

        if (this.displayName.isNotEmpty()) {
            this.fontRenderer.drawString(
                this.displayName,
                this.xSize / 2 - this.fontRenderer.getStringWidth(this.displayName) / 2,
                displayNameOffset,
                Color.DARK_GRAY.rgb
            )
        }
    }

    fun isHovered(x: Int, y: Int, width: Int, height: Int, mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }
}