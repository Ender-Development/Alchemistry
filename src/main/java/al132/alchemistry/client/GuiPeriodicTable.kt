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
        val w = scaledRes.scaledWidth.coerceAtMost(1512)
        val w_scale = w / 1512f
        val h = scaledRes.scaledHeight.coerceAtMost(792)
        val h_scale = h / 792f

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Minecraft.getMinecraft().textureManager.bindTexture(
            ResourceLocation(
                Reference.MODID,
                "textures/gui/periodic_table.png"
            )
        )
        drawScaledCustomSizeModalRect(0, 0, 0f, 0f, w, h, w, h, w.toFloat(), h.toFloat())
        drawCenteredString(fontRenderer, "alchemistry.gui.periodic_table".translate(), width / 2, 24, 0xFFFFFF)

        val boxWidth = 84f * w_scale
        val boxHeight = 84f * h_scale
        val group = (mouseX / boxWidth).toInt() + 1
        val period = (mouseY / boxHeight).toInt() + 1
        // TODO: account for La/Actinides (Hf/... gets replaced by them, + the Y gap in-between)

        println("$group, $period")
        val element = ElementRegistry.getAllElements().find { it.group == group && it.period == period }

        if(element != null)
            drawElementTip(element)

//        val startX = ((width - (boxWidth * 18)) / 2) * w_scale
//        val startY = (((height - (boxHeight * 7)) / 2) - 33.0f) * h_scale
//        println("scale: ${w_scale}, ${h_scale}; boxSize: ${boxWidth}x${boxHeight}; startXY: $startX, $startY")
//        var count = 0

//        println("$mouseX, $mouseY")
//        println("$row, $col")
        
//        ElementRegistry.getAllElements().forEach { element ->
//            var x = startX
//            var y = startY
//            val group = element.group
//            val period = element.period
//
//            if (group == 0 || period == 0) {
//                return@forEach
//            }
//
//            for (row in 1 until 8) {
//                if (period == row) {
//                    for (col in 0 until 19) {
//                        if (group == col) {
//                            if (!((period == 6 || period == 7) && group == 3)) {
//                                if (mouseX >= x && mouseX <= x + boxWidth && mouseY >= y && mouseY <= y + boxHeight) {
//                                    drawElementTip(element)
//                                }
//                            } else {
//                                val resetX = x
//                                val resetY = y
//                                if (period == 6) {
//                                    y = (boxHeight * 7.45f) + startY
//                                    x = (boxWidth * count) + startX + boxWidth * 2
//                                    if (mouseX >= x && mouseX <= x + boxWidth && mouseY >= y && mouseY <= y + boxHeight) {
//                                        drawElementTip(element)
//                                    }
//                                    count++
//                                }
//                                if (period == 7) {
//                                    y = (boxHeight * 8.45f) + startY
//                                    x = (boxWidth * count - 15) + startX + boxWidth * 2
//                                    if (mouseX >= x && mouseX <= x + boxWidth && mouseY >= y && mouseY <= y + boxHeight) {
//                                        drawElementTip(element)
//                                    }
//                                    count++
//                                }
//                                y = resetY
//                                x = resetX
//                            }
//                        }
//                        x += boxWidth
//                    }
//                }
//                x = startX
//                y += boxHeight
//            }
//        }
        GlStateManager.popMatrix()
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    // TODO: fix scaling and overall drawing position
    private fun drawElementTip(element: ChemicalElement) {
        val scaledRes = ScaledResolution(Minecraft.getMinecraft())
        val w = scaledRes.scaledWidth.coerceAtMost(816)
        val h = scaledRes.scaledHeight.coerceAtMost(240)

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Minecraft.getMinecraft().textureManager.bindTexture(
            ResourceLocation(
                Reference.MODID,
                "textures/gui/elements/${element.name}_tooltip.png"
            )
        )
        drawScaledCustomSizeModalRect(((this.width - 276) / 2) - 55, ((this.height - (7 * 28)) / 2) - 30, 0f, 0f, w, h, w, h, w.toFloat(), h.toFloat())
    }
}