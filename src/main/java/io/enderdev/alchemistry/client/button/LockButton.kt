package io.enderdev.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class LockButton(x: Int, y: Int) : AbstractButton(ButtonID.LOCK, x, y) {
	enum class State {
		LOCKED, UNLOCKED
	}

	var isLocked = State.UNLOCKED

	override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
		if(visible) {
			mc.textureManager.bindTexture(textureLocation)
			GlStateManager.color(1F, 1F, 1F)
			val i = if(isLocked == State.LOCKED) 16 else 0
			drawTexturedModalRect(x, y, 64, i, 16, 16)
		}
		super.drawButton(mc, mouseX, mouseY, partialTicks)
	}
}
