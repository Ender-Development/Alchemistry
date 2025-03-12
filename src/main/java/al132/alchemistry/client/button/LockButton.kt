package al132.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

class LockButton(x: Int, y: Int) : AbstractButton(ButtonID.LOCK, x, y) {
    enum class State {
        LOCKED, UNLOCKED
    }

    var isLocked = State.UNLOCKED

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (this.visible) {
            mc.textureManager.bindTexture(textureLocation)
            GlStateManager.color(1F, 1F, 1F);
            val i = if (this.isLocked == State.LOCKED) 16 else 0
            this.drawTexturedModalRect(this.x, this.y, 64, i, 16, 16)
        }
    }
}