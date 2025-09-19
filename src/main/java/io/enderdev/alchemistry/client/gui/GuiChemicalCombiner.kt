package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.button.LockButtonWrapper
import io.enderdev.alchemistry.client.container.ContainerChemicalCombiner
import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import org.ender_development.catalyx.client.gui.BaseGui
import org.ender_development.catalyx.client.gui.wrappers.CapabilityEnergyDisplayWrapper
import org.ender_development.catalyx.utils.extensions.get
import org.ender_development.catalyx.utils.extensions.translate

class GuiChemicalCombiner(playerInv: InventoryPlayer, val tile: TileChemicalCombiner) : BaseGui(ContainerChemicalCombiner(playerInv, tile), tile) {
	lateinit var toggleRecipeLock: LockButtonWrapper

	override val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/chemical_combiner_gui_redox.png")

	init {
		this.displayData.add(CapabilityEnergyDisplayWrapper(8, 21, 16, 70, tile::energyStorage))
	}

	override fun initGui() {
		super.initGui()
		toggleRecipeLock = LockButtonWrapper(this.guiLeft + 175 - 20, this.guiTop + displayNameOffset - 4 + 18)
		this.buttonList.add(toggleRecipeLock.button)
	}

	override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY)

		if(tile.recipeIsLocked) {
			toggleRecipeLock.isLocked = LockButtonWrapper.State.LOCKED
		} else {
			toggleRecipeLock.isLocked = LockButtonWrapper.State.UNLOCKED
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
			drawItemStack(output, x, y, "tile.alchemistry:combiner.target".translate())
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
