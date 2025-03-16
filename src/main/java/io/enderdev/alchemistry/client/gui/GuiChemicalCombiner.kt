package io.enderdev.alchemistry.client.gui

import al132.alib.client.CapabilityEnergyDisplayWrapper
import al132.alib.utils.Translator
import al132.alib.utils.extensions.get
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.client.button.LockButton
import io.enderdev.alchemistry.client.container.ContainerChemicalCombiner
import io.enderdev.alchemistry.network.ButtonPacket
import io.enderdev.alchemistry.network.PacketHandler
import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack

/**
 * Created by al132 on 1/16/2017.
 */
class GuiChemicalCombiner(
    playerInv: InventoryPlayer, tile: TileChemicalCombiner, override val displayNameOffset: Int = 51
) : GuiBase<TileChemicalCombiner>(
    ContainerChemicalCombiner(playerInv, tile), tile, "chemical_combiner"
) {

    lateinit var toggleRecipeLock: LockButton

    init {
        this.displayData.add(CapabilityEnergyDisplayWrapper(8, 64, 16, 70, tile::energyStorage))
    }

    override fun actionPerformed(guibutton: GuiButton) {
        super.actionPerformed(guibutton)
        if (guibutton.id == toggleRecipeLock.id) {
            PacketHandler.INSTANCE!!.sendToServer(ButtonPacket(tile.pos, lock = true))
        }
    }

    override fun initGui() {
        super.initGui()
        toggleRecipeLock = LockButton(this.guiLeft + 175 - 20, this.guiTop + displayNameOffset - 4 + 18)
        this.buttonList.add(toggleRecipeLock)
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)

        if (tile.recipeIsLocked) {
            toggleRecipeLock.isLocked = LockButton.State.LOCKED
        } else {
            toggleRecipeLock.isLocked = LockButton.State.UNLOCKED
        }
    }

    override fun renderTooltips(mouseX: Int, mouseY: Int) {
        super.renderTooltips(mouseX, mouseY)
        if (isHovered(toggleRecipeLock.x, toggleRecipeLock.y, 16, 16, mouseX, mouseY)) {
            if (tile.recipeIsLocked) {
                this.drawHoveringText(listOf(Translator.translateToLocal("tooltip.locked")), mouseX, mouseY)
            } else {
                this.drawHoveringText(listOf(Translator.translateToLocal("tooltip.unlocked")), mouseX, mouseY)
            }
        }
        if (!tile.clientRecipeTarget.getStackInSlot(0).isEmpty) {
            val output = tile.clientRecipeTarget[0]
            val x = (this.width - this.xSize) / 2 + 152
            val y = (this.height - this.ySize) / 2 + 99
            this.drawItemStack(output, x, y, Translator.translateToLocal("tile.combiner.target"))
            if (isHovered(x, y, 16, 16, mouseX, mouseY)) {
                this.drawHoveringText(listOf(output.displayName), mouseX, mouseY)
            }
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        this.mc.textureManager.bindTexture(this.textureLocation)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2
        if (tile.progressTicks > 0) {
            val k = this.getBarScaled(27, tile.progressTicks, ConfigHandler.COMBINER.processingTicks)
            this.drawTexturedModalRect(i + 102, j + 90, 175, 0, k, 36)
        }
    }

    private fun drawItemStack(stack: ItemStack, x: Int, y: Int, text: String?) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.translate(0.0f, 0.0f, 32.0f)
        this.zLevel = 200.0f
        this.itemRender.zLevel = 200.0f
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y)
        this.itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, x, y + 14, text)
        this.zLevel = 0.0f
        this.itemRender.zLevel = 0.0f
    }
}