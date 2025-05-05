package io.enderdev.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class ModeratorButton(x: Int, y: Int) : AbstractButton(ButtonID.MODIFIER, x, y) {
	override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
		if(visible) {
			mc.textureManager.bindTexture(textureLocation)
			GlStateManager.color(1f, 1f, 1f)
			drawTexturedModalRect(x, y, 48, 32, 16, 16)
		}
		super.drawButton(mc, mouseX, mouseY, partialTicks)
	}
}
