package io.enderdev.alchemistry.client.button

import io.enderdev.alchemistry.Tags
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

abstract class AbstractButton(buttonId: ButtonID, val x: Int, val y: Int) : GuiButton(buttonId.ordinal, x, y, 16, 16, "") {
	companion object {
		val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/template_redox.png")
	}

	enum class ButtonID() {
		PAUSE,
		REDSTONE,
		LOCK,
		SINGLE,
		MODIFIER;
	}

	override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
		hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
		if(visible && isMouseOver) {
			mc.textureManager.bindTexture(textureLocation)
			GlStateManager.color(1f, 1f, 1f)
			drawTexturedModalRect(x, y, 48, 48, width, height)
		}
	}
}
