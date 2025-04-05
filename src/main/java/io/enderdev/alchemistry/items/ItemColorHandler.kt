package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.Color
import java.util.*

@SideOnly(Side.CLIENT)
class ItemColorHandler : IItemColor {
	val april: Boolean

	init {
		val calendar = Calendar.getInstance()
		april = calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) == 1
	}

	override fun colorMultiplier(stack: ItemStack, tintIndex: Int): Int {
		val item = stack.item
		val meta = stack.metadata

		val ret = if(tintIndex != 0)
			Color.WHITE
		else if(item is ItemElement)
			if(meta > 118) ElementRegistry[meta]!!.color else Color.WHITE
		else if(item is ItemElementIngot && ElementRegistry.keys().filter { it <= 118 }.contains(meta))
			ElementRegistry[meta]!!.color
		else if(item is ItemCompound && CompoundRegistry.keys().contains(meta))
			CompoundRegistry[meta]!!.color
		else
			Color.BLACK
		return if(april)
			aprilMultiplier(item, meta, ret)
		else
			ret.rgb
	}

	private fun aprilMultiplier(item: Item, meta: Int, original: Color): Int {
		val offset = ((System.currentTimeMillis() shr 9) % 120L).toInt() - 60
		return if(item is ItemElement && meta <= 118)
			Color(195 + offset, 195 + offset, 195 + offset).rgb
		else
			Color(
				(original.red + offset).coerceIn(0, 255),
				(original.green + offset).coerceIn(0, 255),
				(original.blue + offset).coerceIn(0, 255)
			).rgb
	}
}
