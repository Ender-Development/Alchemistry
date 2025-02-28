package al132.alchemistry.client

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
    init {
        this.width = Minecraft.getMinecraft().displayWidth
        this.height = Minecraft.getMinecraft().displayHeight
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

//    val textureLocation = ResourceLocation(Reference.MODID, "textures/images/periodic_table.png")
//    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
//        super.drawScreen(mouseX, mouseY, partialTicks)
//        this.drawDefaultBackground()
//        this.mc.textureManager.bindTexture(this.textureLocation)
//        val scaledRes = ScaledResolution(Minecraft.getMinecraft())
//        val w = Math.min(scaledRes.scaledWidth, 2000)
//        val h = Math.min(scaledRes.scaledHeight, 1016)
//        //drawModalRectWithCustomSizedTexture(0, 0, 0f, 0f,w,h,w.toFloat(),h.toFloat())
//        drawScaledCustomSizeModalRect(0, 0, 0f, 0f, w, h, w, h, w.toFloat(), h.toFloat())
//    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        GlStateManager.pushMatrix()
        drawDefaultBackground()

        val scaledRes = ScaledResolution(Minecraft.getMinecraft())
        val w = scaledRes.scaledWidth.coerceAtMost(1512)
        val wScale = w / 1512f
        val h = scaledRes.scaledHeight.coerceAtMost(792)
        val hScale = h / 792f

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Minecraft.getMinecraft().textureManager.bindTexture(
            ResourceLocation(
                Reference.MODID,
                "textures/gui/periodic_table.png"
            )
        )
        drawScaledCustomSizeModalRect(0, 0, 0f, 0f, w, h, w, h, w.toFloat(), h.toFloat())

        val boxWidth = 84f * wScale
        val boxHeight = 84f * hScale
        val group = (mouseX / boxWidth).toInt() + 1
        var period: Int
        var reverse = false
        if(mouseY > (7 * 84f * hScale)) {
            // La/Actinides
            // have to compensate for the offset
            val y = mouseY - 35f * hScale
            period = (y / boxHeight).toInt() - 1

            // free space in-between
            if(mouseY < ((7 * 84f + 35) * hScale))
                period = -1
            reverse = true
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
        val scaledRes = ScaledResolution(Minecraft.getMinecraft())
        val boxWidth = 84f * scaledRes.scaledWidth.coerceAtMost(1512) / 1512f
        val boxHeight = 84f * scaledRes.scaledWidth.coerceAtMost(792) / 792f
        val w = scaledRes.scaledWidth.coerceAtMost(816)
        val h = scaledRes.scaledHeight.coerceAtMost(240).coerceAtMost((boxHeight * 3).toInt())

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Minecraft.getMinecraft().textureManager.bindTexture(
            ResourceLocation(
                Reference.MODID,
                "textures/gui/elements/${element.name}_tooltip.png"
            )
        )
        drawScaledCustomSizeModalRect((boxWidth * 2).roundToInt(), 0, 0f, 0f, w / 2, h / 2, w / 2, h / 2, w / 2f, h / 2f)
    }
}