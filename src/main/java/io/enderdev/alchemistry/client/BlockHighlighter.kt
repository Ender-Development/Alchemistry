package io.enderdev.alchemistry.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
class BlockHighlighter {
    companion object {
        private var counter = 0
        private var counterDirection = 1

        var pos: BlockPos? = null
            private set
        private var r = 1f
        private var g = 1f
        private var b = 1f
        private var until = 0L

        fun highlightBlock(pos: BlockPos, r: Float, g: Float, b: Float, time: Int) {
            this.pos = pos
            this.r = r
            this.g = g
            this.b = b
            until = System.currentTimeMillis() + time.toLong()
        }

        fun eventHandler(event: RenderWorldLastEvent) {
            if(pos == null)
                return

            val time = System.currentTimeMillis()

            if(time > until) {
                pos = null
                return
            }

            // do this instead of just incrementing a float because precision makes it jittery (float going to like 1.0000001f, gl rolling over and interpreting it as 0f)
            counter += counterDirection
            if(counter == 0 || counter == 50)
                counterDirection *= -1

            val alpha = .5f + counter / 100f

            val p = Minecraft.getMinecraft().player
            val doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.partialTicks
            val doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.partialTicks
            val doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.partialTicks

            GlStateManager.pushMatrix()
            GlStateManager.enableBlend()
            GlStateManager.color(r, g, b, alpha)
            GlStateManager.glLineWidth(3f)
            GlStateManager.translate(-doubleX, -doubleY, -doubleZ)

            GlStateManager.disableDepth()
            GlStateManager.disableTexture2D()

            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.buffer
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR)
            renderOutline(buffer, pos!!.x.toDouble(), pos!!.y.toDouble(), pos!!.z.toDouble(), r, g, b, alpha)

            tessellator.draw()

            GlStateManager.enableTexture2D()
            GlStateManager.enableDepth()
            GlStateManager.disableBlend()
            GlStateManager.popMatrix()
        }

        private fun renderOutline(buffer: BufferBuilder, mx: Double, my: Double, mz: Double, red: Float, green: Float, blue: Float, alpha: Float) {
            buffer.pos(mx, my, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my + 1, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my + 1, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my + 1, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my + 1, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my + 1, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my + 1, mz).color(red, green, blue, alpha).endVertex()

            buffer.pos(mx, my + 1, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my + 1, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my + 1, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my + 1, mz).color(red, green, blue, alpha).endVertex()

            buffer.pos(mx + 1, my, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my, mz).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my + 1, mz).color(red, green, blue, alpha).endVertex()

            buffer.pos(mx, my, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx + 1, my, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my, mz + 1).color(red, green, blue, alpha).endVertex()
            buffer.pos(mx, my + 1, mz + 1).color(red, green, blue, alpha).endVertex()
        }
    }
}