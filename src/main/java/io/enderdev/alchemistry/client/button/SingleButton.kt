package io.enderdev.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class SingleButton(x: Int, y: Int) : AbstractButton(ButtonID.SINGLE, x, y) {
	enum class State {
		SINGLE, REGULAR
	}

	var isSingle = State.REGULAR

	override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
		if(visible) {
			mc.textureManager.bindTexture(textureLocation)
			GlStateManager.color(1F, 1F, 1F)
			val i = if(isSingle == State.SINGLE) 16 else 0
			drawTexturedModalRect(x, y, 80, i, 16, 16)
		}
		super.drawButton(mc, mouseX, mouseY, partialTicks)
	}
}
