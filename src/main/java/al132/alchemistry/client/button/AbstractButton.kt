package al132.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.util.ResourceLocation

abstract class AbstractButton(buttonId: Int, val x: Int, val y: Int): GuiButton(buttonId, x, y, 16, 16, "") {
    companion object {
        val textureLocation = ResourceLocation("alchemistry:textures/gui/container/template.png")
    }

    abstract override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float)
}