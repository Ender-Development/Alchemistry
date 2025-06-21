package io.enderdev.alchemistry.client.gui.misc

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.compat.jei.AlchemistryPlugin
import io.enderdev.alchemistry.utils.BlockMeta
import io.enderdev.catalyx.utils.RenderUtils
import io.enderdev.catalyx.utils.extensions.toStack
import io.enderdev.catalyx.utils.extensions.translate
import mezz.jei.config.KeyBindings
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Loader
import org.lwjgl.input.Mouse
import java.awt.Color

class GuiModifiers(
	val title: String,
	val entries: Collection<Pair<IRenderer, Collection<Pair<String, Color>>>>, // renderers to (values to color)
	val columns: Array<String>,
	val sort: Array<(reverse: Boolean) -> Unit>,
	val returnTo: GuiScreen
) : GuiScreen() {
	private val maxEntries = 7
	private val textColumns = arrayOf(60, 100, 150)
	private var scrollCount = 0
	private var mouseClick: MouseClickData? = null
	private var hoveredEntry: IRenderer? = null

	private fun bind(texture: String = "reactor_modifier_gui_redox") {
		mc.textureManager.bindTexture(ResourceLocation(Tags.MOD_ID, "textures/gui/container/$texture.png"))
		GlStateManager.color(1f, 1f, 1f, 1f)
	}

	override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
		GlStateManager.pushMatrix()
		drawDefaultBackground()

		// bind texture
		bind()
		// calc top left
		val x = (width - 174) shr 1
		val y = (height - 180) shr 1
		// draw texture
		drawTexturedModalRect(x, y, 0, 0, 175, 181)
		// draw title
		fontRenderer.drawString(title, (width - fontRenderer.getStringWidth(title)) shr 1, y + 8, Color.DARK_GRAY.rgb)

		var drawTooltip = { }

		// top text
		columns.forEachIndexed { idx, text ->
			val half = fontRenderer.getStringWidth(text) shr 1
			val left = x + textColumns[idx] - half
			val top = y + 20
			fontRenderer.drawString(text, left, top, Color.DARK_GRAY.rgb)

			// draw sort tooltip if hovered
			if(sort.isEmpty())
				return@forEachIndexed

			val right = x + textColumns[idx] + half
			val bottom = y + 20 + fontRenderer.FONT_HEIGHT

			if(mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom)
				drawTooltip = {
					drawHoveringText(listOf("tooltip.sort.1".translate(), "tooltip.sort.2".translate()), mouseX, mouseY)
				}

			// if clicked, check if it was us, and if yes, sort the entry list accordingly
			mouseClick?.let {
				if(it.x >= left && it.x <= right && it.y >= top && it.y <= bottom)
					if(it.btn == 0)
						sort[idx](false)
					else if(it.btn == 1)
						sort[idx](true)
			}
		}
		val textOffset = fontRenderer.getCharWidth('0')

		// actually draw stuff
		var offY = 20 + fontRenderer.FONT_HEIGHT + 2
		var totalEntriesDrawn = 0
		var offset = scrollCount

		for((renderer, columns) in entries) {
			if(offset-- > 0)
				continue
			bind()
			drawTexturedModalRect(x + 7, y + offY - 1, renderer.textureX, 0, 18, 18)
			renderer.render(x, y, offY)
			columns.forEachIndexed { idx, (text, color) ->
				fontRenderer.drawString(
					text,
					x + textColumns[idx] - (fontRenderer.getStringWidth(this.columns[idx]) shr 1),
					y + offY + 8 - (fontRenderer.FONT_HEIGHT shr 1),
					color.rgb
				)
			}
			if(mouseX >= x + 8 && mouseX <= x + 24 && mouseY >= y + offY && mouseY <= y + offY + 16) {
				drawTooltip = { renderer.renderTooltip(mouseX, mouseY) }
				hoveredEntry = renderer
			}
			offY += 20
			// the GUI can only handle up to 7 entries
			if(++totalEntriesDrawn == maxEntries)
				break
		}

		bind("template_redox")
		// if we can scroll down, draw an arrow indicating that to the player
		if(totalEntriesDrawn == maxEntries && entries.size - maxEntries > scrollCount)
			drawTexturedModalRect(x + 13, y + 170, 48, 64, 6, 6)

		// if we can scroll up, …
		if(totalEntriesDrawn == maxEntries && scrollCount > 0)
			drawTexturedModalRect(x + 13, y + 22, 54, 64, 6, 6)

		// only draw tooltip after everything else
		drawTooltip()

		GlStateManager.popMatrix()

		mouseClick = null
	}

	override fun doesGuiPauseGame() = false

	override fun handleMouseInput() {
		super.handleMouseInput()
		val scroll = Mouse.getEventDWheel()
		if(scroll != 0) {
			if(scroll > 0) {
				if(scrollCount != 0)
					--scrollCount
			} else {
				if(entries.size - maxEntries > scrollCount)
					++scrollCount
			}
		}
	}

	override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
		mouseClick = MouseClickData(mouseX, mouseY, mouseButton)
	}

	override fun keyTyped(typedChar: Char, keyCode: Int) {
		// inventory keybind or Esc go back to previous gui
		if(mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) || keyCode == 1) {
			mc.displayGuiScreen(returnTo)
			return
		}

		// JEI integration stuff
		if(Loader.isModLoaded("jei"))
			hoveredEntry.apply {
				val showUses = KeyBindings.showUses.isActiveAndMatches(keyCode)
				val show = showUses || KeyBindings.showRecipe.isActiveAndMatches(keyCode)
				if(!show)
					return@apply
				if(this is BlockRenderer)
					AlchemistryPlugin.showRecipes(stack, showUses)
				if(this is FluidRenderer)
					AlchemistryPlugin.showRecipes(stack, showUses)
			}
	}

	data class MouseClickData(val x: Int, val y: Int, val btn: Int)

	interface IRenderer {
		val textureX: Int
		fun render(x: Int, y: Int, offY: Int)
		fun renderTooltip(mouseX: Int, mouseY: Int)
	}

	class BlockRenderer(block: BlockMeta, val self: GuiScreen) : IRenderer {
		override val textureX = 193
		val stack = block.block.toStack(meta = block.meta ?: 0)
		override fun render(x: Int, y: Int, offY: Int) =
			self.itemRender.renderItemAndEffectIntoGUI(stack, x + 8, y + offY)

		override fun renderTooltip(mouseX: Int, mouseY: Int) =
			self.renderToolTip(stack, mouseX, mouseY)
	}

	class FluidRenderer(val fluid: Fluid, val self: GuiScreen) : IRenderer {
		override val textureX = 175
		val stack = FluidStack(fluid, 1)
		override fun render(x: Int, y: Int, offY: Int) =
			RenderUtils.renderGuiTank(stack, 1, 1, x + 8.0, y + offY.toDouble(), 1.0, 16.0, 16.0)

		override fun renderTooltip(mouseX: Int, mouseY: Int) =
			self.drawHoveringText(fluid.getLocalizedName(stack), mouseX, mouseY)
	}
}
