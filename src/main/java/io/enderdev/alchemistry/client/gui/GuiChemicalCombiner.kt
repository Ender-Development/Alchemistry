package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.button.LockButton
import io.enderdev.alchemistry.client.container.ContainerChemicalCombiner
import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import io.enderdev.catalyx.utils.extensions.get
import io.enderdev.catalyx.utils.extensions.translate
import io.enderdev.catalyx.client.gui.BaseGui
import io.enderdev.catalyx.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class GuiChemicalCombiner(playerInv: InventoryPlayer, tile: TileChemicalCombiner) : BaseGui<TileChemicalCombiner>(ContainerChemicalCombiner(playerInv, tile), tile, "chemical_combiner") {
	lateinit var toggleRecipeLock: LockButton

	override val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/${guiName}_gui_redox.png")

	init {
		this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
	}

	override fun initGui() {
		super.initGui()
		toggleRecipeLock = LockButton(this.guiLeft + 175 - 20, this.guiTop + displayNameOffset - 4 + 18)
		this.buttonList.add(toggleRecipeLock)
	}

	override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY)

		if(tile.recipeIsLocked) {
			toggleRecipeLock.isLocked = LockButton.State.LOCKED
		} else {
			toggleRecipeLock.isLocked = LockButton.State.UNLOCKED
		}
	}

	override fun renderTooltips(mouseX: Int, mouseY: Int) {
		super.renderTooltips(mouseX, mouseY)
		if(isHovered(toggleRecipeLock.x, toggleRecipeLock.y, 16, 16, mouseX, mouseY)) {
			if(tile.recipeIsLocked) {
				this.drawHoveringText(listOf("tooltip.locked".translate()), mouseX, mouseY)
			} else {
				this.drawHoveringText(listOf("tooltip.unlocked".translate()), mouseX, mouseY)
			}
		}
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
		drawProgressBar(102, 47, 175, 0, 27, 36)

		if(!tile.clientRecipeTarget.getStackInSlot(0).isEmpty) {
			val output = tile.clientRecipeTarget[0]
			val x = (width - xSize) / 2 + 152
			val y = (height - ySize) / 2 + 56
			drawItemStack(output, x, y, "tile.combiner.target".translate())
			if(isHovered(x, y, 16, 16, mouseX, mouseY))
				renderToolTip(output, mouseX, mouseY)
		}
	}

	private fun drawItemStack(stack: ItemStack, x: Int, y: Int, text: String?) {
		RenderHelper.enableGUIStandardItemLighting()
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
