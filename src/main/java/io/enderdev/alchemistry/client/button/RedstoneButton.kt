package io.enderdev.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class RedstoneButton(x: Int, y: Int) : AbstractButton(ButtonID.REDSTONE, x, y) {
	enum class State {
		ON, OFF
	}

	var needsPower = State.OFF

	override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
		if(this.visible) {
			mc.textureManager.bindTexture(textureLocation)
			GlStateManager.color(1F, 1F, 1F)
			val i = if(this.needsPower == State.ON) 16 else 0
			this.drawTexturedModalRect(this.x, this.y, 96, i, 16, 16)
		}
	}
}
