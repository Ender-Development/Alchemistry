package io.enderdev.alchemistry.compat.jei

import io.enderdev.alchemistry.client.gui.GuiReactorModifiers
import io.enderdev.alchemistry.client.gui.renderer.BlockRenderer
import io.enderdev.alchemistry.client.gui.renderer.FluidRenderer
import io.enderdev.alchemistry.client.gui.renderer.IRenderer
import mezz.jei.config.KeyBindings
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

class InputHandler {
	private var showRecipe: Boolean = false
	private var showUses: Boolean = false

	@SubscribeEvent
	fun keyInputEvent(event: GuiScreenEvent.KeyboardInputEvent.Pre) {
		if(handleKeyEvent() && event.gui is GuiReactorModifiers) {
			val gui: GuiReactorModifiers = event.gui as GuiReactorModifiers
			val renderer: IRenderer = gui.getCurrentRenderer() ?: return
			if(renderer is BlockRenderer)
				AlchemistryPlugin.showRecipes(renderer.stack, showUses)
			if(renderer is FluidRenderer)
				AlchemistryPlugin.showRecipes(renderer.stack, showUses)
		}
	}

	private fun handleKeyEvent(): Boolean {
		val typedChar: Char = Keyboard.getEventCharacter()
		val eventKey: Int = Keyboard.getEventKey()
		return (eventKey == 0 && typedChar >= ' ' || Keyboard.getEventKeyState()) && handleKeyDown(typedChar, eventKey)
	}

	private fun handleKeyDown(typedChar: Char, eventKey: Int): Boolean {
		showRecipe = KeyBindings.showRecipe.isActiveAndMatches(eventKey)
		showUses = KeyBindings.showUses.isActiveAndMatches(eventKey)
		return showRecipe || showUses
	}
}
