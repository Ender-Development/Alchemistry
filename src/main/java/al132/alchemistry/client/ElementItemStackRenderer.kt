package al132.alchemistry.client

import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ItemElement
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class ElementItemStackRenderer : TileEntityItemStackRenderer() {
    companion object {
        val instance = ElementItemStackRenderer()
        val mc: Minecraft = Minecraft.getMinecraft()
    }

    override fun renderByItem(stack: ItemStack, partialTicks: Float) {
        val item: Item = stack.item
        val model: IBakedModel = mc.renderItem.itemModelMesher.getItemModel(stack)
        if (item is ItemElement) {
            GlStateManager.pushMatrix()
            mc.renderItem.renderModel(model, stack)
            val abbreviation = ElementRegistry[stack.metadata]?.abbreviation
            mc.fontRenderer.drawString(abbreviation!!, 0, 0, 0xFFFFFF)
            GlStateManager.popMatrix()
        }
    }

    fun get(): ElementItemStackRenderer {
        return instance
    }
}