package al132.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class PauseButton(buttonId: Int, x: Int, y: Int) : AbstractButton(buttonId, x, y) {
    enum class State {
        PAUSED, RUNNING
    }

    var isPaused = State.RUNNING

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (this.visible) {
            mc.textureManager.bindTexture(textureLocation)
            GlStateManager.color(1F, 1F, 1F);
            val i = if (this.isPaused == State.PAUSED) 16 else 0
            this.drawTexturedModalRect(this.x, this.y, 48, i, 16, 16)
        }
    }
}