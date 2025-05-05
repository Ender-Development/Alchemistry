package io.enderdev.alchemistry.client.gui

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.gui.GuiReactorModifiers.*
import io.enderdev.alchemistry.compat.jei.AlchemistryPlugin
import io.enderdev.alchemistry.tiles.TileEvaporator
import io.enderdev.alchemistry.utils.extensions.translate
import mezz.jei.config.KeyBindings
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Loader
import org.lwjgl.input.Mouse
import java.awt.Color

class GuiHeatSources(val previousGUI: GuiEvaporator) : GuiScreen() {
	// a dumbed down version of GuiReactorModifiers
	// TODO: this gui is very empty right meow, some ideas:
	// - GlStateManager.scale the background down by 0.5, make the title text slightly smaller (remove previousGUI.displayName), profit
	// - make it have 2 columns
	private val maxEntries = 7
	private var scrollCount = 0
	private val entries: MutableList<Pair<IRenderer, Double>> = mutableListOf()
	private var hoveredEntry: IRenderer? = null

	init {
		entries.addAll(TileEvaporator.heatSources.map { (moderator, multiplier) ->
			val fluid = FluidRegistry.lookupFluidForBlock(moderator.block)
			(if(fluid != null)
				FluidRenderer(fluid, this)
			else
				BlockRenderer(moderator, this)) to multiplier
		})
	}

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
		val title = "${previousGUI.displayName} ${"tile.evaporator.heat_sources".translate()}"
		fontRenderer.drawString(title, (width - fontRenderer.getStringWidth(title)) shr 1, y + 8, Color.DARK_GRAY.rgb)

		var drawTooltip = { }

		// top text
		fontRenderer.drawString("tile.evaporator.heat".translate(""), x + 32, y + 20, Color.DARK_GRAY.rgb)

		// actually draw stuff
		var offY = 20 + fontRenderer.FONT_HEIGHT + 2
		var totalEntriesDrawn = 0
		var offset = scrollCount

		for((renderer, heat) in entries) {
			if(offset-- > 0)
				continue
			bind()
			drawTexturedModalRect(x + 7, y + offY - 1, renderer.textureX, 0, 18, 18)
			renderer.render(x, y, offY)
			fontRenderer.drawString("${Alchemistry.DECIMAL_FORMAT.format(heat)}x", x + 34, y + offY + 8 - (fontRenderer.FONT_HEIGHT shr 1), previousGUI.getColor(heat))
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

	override fun keyTyped(typedChar: Char, keyCode: Int) {
		// inventory keybind or Esc go back to previous gui
		if(mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode) || keyCode == 1) {
			mc.displayGuiScreen(previousGUI)
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
}
