package al132.alchemistry.client

import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ItemElement
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.ItemModelMesher
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
class ElementItemStackRenderer : TileEntityItemStackRenderer() {
    companion object {
        val instance = ElementItemStackRenderer()
        val mc: Minecraft = Minecraft.getMinecraft()
    }

    override fun renderByItem(stack: ItemStack, partialTicks: Float) {
        val item: Item = stack.item
        var model: IBakedModel = getBakedModel(stack)
        if (item is ItemElement) {
            GlStateManager.enableRescaleNormal()
            GlStateManager.enableAlpha()
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F)
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
            GlStateManager.disableLighting()

            GlStateManager.pushMatrix()
            model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false)
            GlStateManager.translate(-0.5, -0.5, -0.5)
            mc.renderItem.renderModel(model, stack)
            GlStateManager.popMatrix()

            val abbreviation: String? = ElementRegistry[stack.metadata]?.abbreviation
            val fontRenderer: FontRenderer = getFontRenderer(stack)

            GlStateManager.pushMatrix()
            GlStateManager.scale(0.05, -0.05, 0.0)
            GlStateManager.translate(0.0, -20.0, 0.0)
            fontRenderer.drawString(abbreviation!!, 0, 0, 0xFFFFFF)
            GlStateManager.scale(20.0, -20.0, 0.0)
            GlStateManager.enableLighting()
            GlStateManager.popMatrix()

            GlStateManager.disableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.disableRescaleNormal()
        }
    }

    fun get(): ElementItemStackRenderer {
        return instance
    }

    private fun getBakedModel(itemStack: ItemStack): IBakedModel {
        val itemModelMesher: ItemModelMesher = mc.renderItem.itemModelMesher
        val bakedModel: IBakedModel = itemModelMesher.getItemModel(itemStack)
        return bakedModel.getOverrides().handleItemState(bakedModel, itemStack, null, null)
    }

    private fun getFontRenderer(itemStack: ItemStack): FontRenderer {
        val item: Item = itemStack.item;
        return item.getFontRenderer(itemStack) ?: mc.fontRenderer
    }
}