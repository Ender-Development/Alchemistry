package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color

@SideOnly(Side.CLIENT)
class ItemColorHandler : IItemColor {

    override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {
        val item = stack.item
        val meta = stack.metadata

        return if(tintIndex != 0)
            Color.WHITE.rgb
        else if (item is ItemElement)
            if (meta > 118) ElementRegistry[meta]!!.color.rgb else Color.WHITE.rgb
        else if (item is ItemElementIngot && ElementRegistry.keys().filter { it <= 118 }.contains(meta))
            ElementRegistry[meta]!!.color.rgb
        else if (item is ItemCompound && CompoundRegistry.keys().contains(meta))
            CompoundRegistry[meta]!!.color.rgb
        else
            Color.BLACK.rgb
    }
}