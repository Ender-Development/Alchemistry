package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.gui.renderer.BlockRenderer
import io.enderdev.alchemistry.client.gui.renderer.FluidRenderer
import io.enderdev.alchemistry.client.gui.renderer.IRenderer
import io.enderdev.alchemistry.compat.jei.AlchemistryPlugin
import io.enderdev.alchemistry.tiles.AbstractReactorController.BlockMeta
import io.enderdev.alchemistry.tiles.AbstractReactorController.Multiplier
import io.enderdev.alchemistry.utils.RenderUtils
import io.enderdev.alchemistry.utils.extensions.toStack
import io.enderdev.alchemistry.utils.extensions.translate
import mezz.jei.config.KeyBindings
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Loader
import org.lwjgl.input.Mouse
import java.awt.Color

class GuiReactorModifiers(val parentGuiReactorController: GuiReactorController<*>, val fluidModifiers: Map<Fluid, Multiplier>, val blockModifiers: Map<BlockMeta, Multiplier>) : GuiScreen() {
	private val minecraftGray = 4210752
	private val maxEntries = 7
	private val textColumns = listOf(60, 105, 150)
	private var scrollCount = 0
	private val entries: MutableList<Pair<IRenderer, Multiplier>> = mutableListOf()
	private var mouseClick: MouseClickData? = null
	private var hoveredEntry: IRenderer? = null

	private val sortBy = listOf(
		{ multiplier: Multiplier -> multiplier.productivity },
		{ multiplier: Multiplier -> multiplier.processingTime },
		{ multiplier: Multiplier -> multiplier.energy }
	)

	init {
		entries.addAll(fluidModifiers.map { (fluid, multiplier) -> FluidRenderer(fluid, this) to multiplier })
		entries.addAll(blockModifiers.map { (block, multiplier) -> BlockRenderer(block, this) to multiplier })
	}

	private fun bind(texture: String) {
		mc.textureManager.bindTexture(ResourceLocation(Tags.MOD_ID, "textures/gui/container/${texture}.png"))
		GlStateManager.color(1f, 1f, 1f, 1f)
	}

	override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
		GlStateManager.pushMatrix()
		drawDefaultBackground()

		// bind texture
		bind("reactor_modifier_gui_redox")
		// calc top left
		val x = (width - 174) shr 1
		var y = (height - 180) shr 1
		// draw texture
		drawTexturedModalRect(x, y, 0, 0, 175, 181)
		// draw title
		val title = parentGuiReactorController.displayName + " " + "tile.reactor.modifers".translate()
		fontRenderer.drawString(title, width / 2 - fontRenderer.getStringWidth(title) / 2, y + 8, minecraftGray)
		y += 12

		var drawTooltip = { }

		// top text
		listOf("output_multiplier", "processing_time", "energy_consumption").forEachIndexed { idx, it ->
			val text = "tile.reactor.$it.short".translate("")
			val half = fontRenderer.getStringWidth(text) shr 1
			fontRenderer.drawString(text, x + textColumns[idx] - half, y + 8, minecraftGray)

			// draw tooltip if hovered
			val left = x + textColumns[idx] - half
			val top = y + 8
			val right = x + textColumns[idx] + half
			val bottom = y + 8 + fontRenderer.FONT_HEIGHT

			if(mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom)
				drawTooltip = {
					drawHoveringText(listOf("tooltip.sort.1".translate(), "tooltip.sort.2".translate()), mouseX, mouseY)
				}

			// if clicked, check if it was us, and if yes, sort the entry list accordingly
			mouseClick?.let {
				if(mouseClick!!.x >= left && mouseClick!!.x <= right && mouseClick!!.y >= top && mouseClick!!.y <= bottom) {
					val sorter = { pair: Pair<IRenderer, Multiplier> -> sortBy[idx](pair.second) }
					if(mouseClick!!.btn == 0)
						entries.sortBy(sorter)
					else if(mouseClick!!.btn == 1)
						entries.sortByDescending(sorter)
				}
			}
		}

		// draw modifiers
		// negative and positive are usually the exact same because tabular numbers, but if someone's using a custom font or something cursed, doesn't hurt to separate
		val negativeOffset = fontRenderer.getCharWidth('-')
		val positiveOffset = fontRenderer.getCharWidth('+')
		val textOffset = fontRenderer.getCharWidth('0') * 2

		val drawMultiplier = { multiplier: Multiplier, offY: Int ->
			listOf(multiplier.productivity, multiplier.processingTime, multiplier.energy).forEachIndexed { idx, it ->
				val text = "${if(it < 0) "" else "+"}${Alchemistry.DECIMAL_FORMAT.format(it)}x"
				fontRenderer.drawString(
					text,
					x + textColumns[idx] - textOffset - if(it < 0) negativeOffset else positiveOffset,
					y + offY + 8 - (fontRenderer.FONT_HEIGHT shr 1),
					GuiReactorController.getColorFromValue(it + 1, idx != 0)
				)
			}
		}

		// actually draw stuff
		var offY = 8 + fontRenderer.FONT_HEIGHT + 2
		var totalEntriesDrawn = 0
		var offset = scrollCount

		for((renderer, multiplier) in entries) {
			if(offset-- > 0)
				continue
			bind("reactor_modifier_gui_redox")
			drawTexturedModalRect(x + 7, y + offY - 1, renderer.textureX, 0, 18, 18)
			renderer.render(x, y, offY)
			drawMultiplier(multiplier, offY)
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
		// if we can scroll down, draw a 'v' indicating that to the player
		if(totalEntriesDrawn == maxEntries && fluidModifiers.size + blockModifiers.size - maxEntries > scrollCount)
			drawTexturedModalRect(x + 13, y + 158, 48, 64, 6, 6)

		// if we can scroll up, …
		if(totalEntriesDrawn == maxEntries && scrollCount > 0)
			drawTexturedModalRect(x + 13, y + 10, 54, 64, 6, 6)

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
				if(fluidModifiers.size + blockModifiers.size - maxEntries > scrollCount)
					++scrollCount
			}
		}
	}

	override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
		mouseClick = MouseClickData(mouseX, mouseY, mouseButton)
	}

	override fun keyTyped(typedChar: Char, keyCode: Int) {
		if(mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
			mc.displayGuiScreen(parentGuiReactorController)
		if(Loader.isModLoaded("jei")) {
			if(KeyBindings.showRecipe.isActiveAndMatches(keyCode) || KeyBindings.showUses.isActiveAndMatches(keyCode)) {
				val uses: Boolean = KeyBindings.showUses.isActiveAndMatches(keyCode)
				val renderer: IRenderer = getCurrentRenderer() ?: return
				if(renderer is BlockRenderer)
					AlchemistryPlugin.showRecipes(renderer.stack, uses)
				if(renderer is FluidRenderer)
					AlchemistryPlugin.showRecipes(renderer.stack, uses)
			}
		}
		super.keyTyped(typedChar, keyCode)
	}

	private data class MouseClickData(val x: Int, val y: Int, val btn: Int)

	fun renderItemAndEffectIntoGUI(stack: ItemStack, x: Int, y: Int) = itemRender.renderItemAndEffectIntoGUI(stack, x, y)

	public override fun renderToolTip(stack: ItemStack, x: Int, y: Int) = super.renderToolTip(stack, x, y)

	fun getCurrentRenderer(): IRenderer? = hoveredEntry
}
