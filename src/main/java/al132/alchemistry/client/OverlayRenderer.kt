package al132.alchemistry.client

import al132.alchemistry.chemistry.ElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
object OverlayRenderer {
    val fontRenderer: FontRenderer = Minecraft.getMinecraft().fontRenderer

    fun renderJeiOverlay(fontRenderer: FontRenderer?, meta: Int, x: Int, y: Int) {
        val scalingFactor = 0.8;
        GlStateManager.pushMatrix()
        GlStateManager.disableDepth()
        GlStateManager.scale(scalingFactor, scalingFactor, 0.0);
        drawAbbreviation(fontRenderer ?: this.fontRenderer, meta, x * (1.0 / scalingFactor), y * (1.0 / scalingFactor))
        GlStateManager.scale(1.0 / scalingFactor, 1.0 / scalingFactor, 0.0)
        GlStateManager.enableDepth()
        GlStateManager.popMatrix()
    }

    fun renderItem(meta: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GlStateManager.disableDepth()
        GlStateManager.scale(0.05, -0.05, 0.0)
        drawAbbreviation(fontRenderer, meta, -10.0, -10.0)
        GlStateManager.scale(20.0, -20.0, 0.0)
        GlStateManager.enableDepth()
        GlStateManager.enableCull()
        GlStateManager.enableLighting()
        GlStateManager.depthMask(true)
        GlStateManager.popMatrix()
    }

    private fun drawAbbreviation(fontRenderer: FontRenderer, meta: Int, x: Double, y: Double) {
        val abbreviation: String = ElementRegistry.abbreviation(meta)
        fontRenderer.drawString(abbreviation, x.toFloat(), y.toFloat(), 0xFFFFFF, false);
    }
}