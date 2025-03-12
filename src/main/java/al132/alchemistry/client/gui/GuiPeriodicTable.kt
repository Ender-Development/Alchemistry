package al132.alchemistry.client.gui

import al132.alchemistry.Reference
import al132.alchemistry.chemistry.ChemicalElement
import al132.alchemistry.chemistry.ElementRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import kotlin.math.roundToInt

class GuiPeriodicTable : GuiScreen() {
    val minecraft: Minecraft = Minecraft.getMinecraft()

    init {
        width = minecraft.displayWidth
        height = minecraft.displayHeight
    }

    companion object {
        // period_table.png size
        const val IMAGE_WIDTH = 1512
        const val IMAGE_HEIGHT = 792

        // element box size
        const val BOX_WIDTH = 84
        const val BOX_HEIGHT = 84

        // offset between the main table and La/Actinides
        const val ACTINIDES_OFFSET = 35

        // size of the element tooltip pngs
        const val TOOLTIP_WIDTH = 816
        const val TOOLTIP_HEIGHT = 240
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.pushMatrix()
        drawDefaultBackground()

        val scaledRes = ScaledResolution(minecraft)
        val w = scaledRes.scaledWidth.coerceAtMost(IMAGE_WIDTH)
        val wScale = w / IMAGE_WIDTH.toFloat()
        val h = scaledRes.scaledHeight.coerceAtMost(IMAGE_HEIGHT)
        val hScale = h / IMAGE_HEIGHT.toFloat()

        GlStateManager.color(1f, 1f, 1f, 1f)
        minecraft.textureManager.bindTexture(ResourceLocation(Reference.MODID, "textures/gui/periodic_table.png"))
        drawScaledCustomSizeModalRect(0, 0, 0f, 0f, w, h, w, h, w.toFloat(), h.toFloat())

        val boxWidth = BOX_WIDTH * wScale
        val boxHeight = BOX_HEIGHT * hScale
        var group = (mouseX / boxWidth).toInt() + 1
        var period: Int
        var reverse = false
        if(mouseY > 7 * boxHeight) {
            val offsetScaled = ACTINIDES_OFFSET * hScale
            // La/Actinides
            // have to compensate for the offset
            val y = mouseY - offsetScaled
            period = (y / boxHeight).toInt() - 1
            ++group
            reverse = true

            // free space in-between
            if(mouseY < 7 * boxHeight + offsetScaled)
                period = -1
        } else
            period = (mouseY / boxHeight).toInt() + 1

        val elements = ElementRegistry.getAllElements()
        val predicate = { el: ChemicalElement -> el.group == group && el.period == period }
        val element = if(reverse) elements.find(predicate) else elements.findLast(predicate)

        if(element != null)
            drawElementTip(element)

        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun drawElementTip(element: ChemicalElement) {
        val scaledRes = ScaledResolution(minecraft)
        val boxWidth = BOX_WIDTH.toFloat() * scaledRes.scaledWidth.coerceAtMost(IMAGE_WIDTH) / IMAGE_WIDTH
        val boxHeight = BOX_HEIGHT.toFloat() * scaledRes.scaledHeight.coerceAtMost(IMAGE_HEIGHT) / IMAGE_HEIGHT
        val h = scaledRes.scaledHeight.coerceAtMost(TOOLTIP_HEIGHT).coerceAtMost((boxHeight * 2.5f).roundToInt())
        val w = (h * TOOLTIP_WIDTH.toFloat() / TOOLTIP_HEIGHT).roundToInt()

        GlStateManager.color(1f, 1f, 1f, 1f)
        minecraft.textureManager.bindTexture(ResourceLocation(Reference.MODID, "textures/gui/elements/${element.name}_tooltip.png"))
        drawScaledCustomSizeModalRect((boxWidth * 2).roundToInt(), 0, 0f, 0f, w, h,w, h, w.toFloat(), h.toFloat())
    }
}