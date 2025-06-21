package io.enderdev.alchemistry.client.button

import io.enderdev.alchemistry.Tags
import io.enderdev.catalyx.client.button.AbstractButton
import io.enderdev.catalyx.client.button.CatalyxButtons
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class LockButton(x: Int, y: Int) : AbstractButton(buttonId, x, y) {
	companion object {
		val buttonId = CatalyxButtons.nextId()
	}
	override val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/template_redox.png")

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
