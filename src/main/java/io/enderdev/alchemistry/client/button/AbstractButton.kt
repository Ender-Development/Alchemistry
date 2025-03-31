package io.enderdev.alchemistry.client.button

import io.enderdev.alchemistry.Tags
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.util.ResourceLocation

abstract class AbstractButton(buttonId: ButtonID, val x: Int, val y: Int): GuiButton(buttonId.ordinal, x, y, 16, 16, "") {
    companion object {
        val textureLocation = ResourceLocation(Tags.MOD_ID, "textures/gui/container/template_redox.png")
    }

    enum class ButtonID(i: Int) {
        PAUSE(0),
        REDSTONE(1),
        LOCK(2),
        SINGLE(3);
    }

    abstract override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partialTicks: Float)
}