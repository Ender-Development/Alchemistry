package al132.alchemistry.client

import al132.alchemistry.Reference
import al132.alchemistry.chemistry.ChemicalElement
import al132.alchemistry.chemistry.ElementRegistry
import al132.alib.utils.extensions.translate
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

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
        val w = scaledRes.scaledWidth.coerceAtMost(2000)
        val w_scale = w / 2000.0
        val h = scaledRes.scaledHeight.coerceAtMost(1016)
        val h_scale = h / 1016.0

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Minecraft.getMinecraft().textureManager.bindTexture(
            ResourceLocation(
                Reference.MODID,
                "textures/gui/periodic_table.png"
            )
        )
        drawScaledCustomSizeModalRect(0, 0, 0f, 0f, w, h, w, h, w.toFloat(), h.toFloat())
        drawCenteredString(fontRenderer, "alchemistry.gui.periodic_table".translate(), width / 2, 24, 0xFFFFFF)

        val boxWidth = 27.75f * w_scale
        val boxHeight = 26.9f * h_scale
        val startX = ((width - (boxWidth * 18)) / 2) * w_scale
        val startY = (((height - (boxHeight * 7)) / 2) - 33.0f) * h_scale
        var count = 0

        ElementRegistry.getAllElements().forEach { element ->
            var x = startX
            var y = startY
            val group = element.group
            val period = element.period

            if (group == 0 || period == 0) {
                return@forEach
            }

            for (row in 1 until 8) {
                if (period == row) {
                    for (col in 0 until 19) {
                        if (group == col) {
                            if (!((period == 6 || period == 7) && group == 3)) {
                                if (mouseX >= x && mouseX <= x + boxWidth && mouseY >= y && mouseY <= y + boxHeight) {
                                    drawElementTip(element)
                                }
                            } else {
                                val resetX = x
                                val resetY = y
                                if (period == 6) {
                                    y = (boxHeight * 7.45f) + startY
                                    x = (boxWidth * count) + startX + boxWidth * 2
                                    if (mouseX >= x && mouseX <= x + boxWidth && mouseY >= y && mouseY <= y + boxHeight) {
                                        drawElementTip(element)
                                    }
                                    count++
                                }
                                if (period == 7) {
                                    y = (boxHeight * 8.45f) + startY
                                    x = (boxWidth * count - 15) + startX + boxWidth * 2
                                    if (mouseX >= x && mouseX <= x + boxWidth && mouseY >= y && mouseY <= y + boxHeight) {
                                        drawElementTip(element)
                                    }
                                    count++
                                }
                                y = resetY
                                x = resetX
                            }
                        }
                        x += boxWidth
                    }
                }
                x = startX
                y += boxHeight
            }
        }
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun drawElementTip(element: ChemicalElement) {
        val scaledRes = ScaledResolution(Minecraft.getMinecraft())
        val w = scaledRes.scaledWidth.coerceAtMost(816)
        val h = scaledRes.scaledHeight.coerceAtMost(240)

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Minecraft.getMinecraft().textureManager.bindTexture(
            ResourceLocation(
                Reference.MODID,
                String.format("textures/gui/elements/%s_tooltip.png", element.name)
            )
        )
        drawScaledCustomSizeModalRect(((this.width - 276) / 2) - 55, ((this.height - (7 * 28)) / 2) - 30, 0f, 0f, w, h, w, h, w.toFloat(), h.toFloat())
    }
}