package io.enderdev.alchemistry.client.button

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.util.ResourceLocation

abstract class AbstractButton(buttonId: ButtonID, val x: Int, val y: Int): GuiButton(buttonId.ordinal, x, y, 16, 16, "") {
    companion object {
        val textureLocation = ResourceLocation("alchemistry:textures/gui/container/template.png")
    }

    enum class ButtonID(i: Int) {
        PAUSE(0),
        LOCK(1),
        SINGLE(2);
    }

    abstract override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float)
}