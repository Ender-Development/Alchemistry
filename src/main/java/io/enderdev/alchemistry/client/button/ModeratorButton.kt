package io.enderdev.alchemistry.client.button

import io.enderdev.alchemistry.Tags
import io.enderdev.catalyx.client.button.AbstractButton
import io.enderdev.catalyx.client.button.CatalyxButtons
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class ModeratorButton(x: Int, y: Int) : AbstractButton(buttonId, x, y) {
	companion object {
		val buttonId = CatalyxButtons.nextId()
	}
	override val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/template_redox.png")

	override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
		if(visible) {
			mc.textureManager.bindTexture(textureLocation)
			GlStateManager.color(1f, 1f, 1f)
			drawTexturedModalRect(x, y, 48, 32, 16, 16)
		}
		super.drawButton(mc, mouseX, mouseY, partialTicks)
	}
}
