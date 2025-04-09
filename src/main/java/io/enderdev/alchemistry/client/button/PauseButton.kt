package io.enderdev.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class PauseButton(x: Int, y: Int) : AbstractButton(ButtonID.PAUSE, x, y) {
	enum class State {
		PAUSED, RUNNING
	}

	var isPaused = State.RUNNING

	override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
		if(visible) {
			mc.textureManager.bindTexture(textureLocation)
			GlStateManager.color(1F, 1F, 1F)
			val i = if(isPaused == State.PAUSED) 16 else 0
			this.drawTexturedModalRect(x, y, 48, i, 16, 16)
		}
		super.drawButton(mc, mouseX, mouseY, partialTicks)
	}
}
