package io.enderdev.alchemistry.items

import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class TooltipItemBlock(block: Block, val tooltip: String) : ItemBlock(block) {
	override fun addInformation(stack: ItemStack, world: World?, tooltips: List<String>, flag: ITooltipFlag) {
		(tooltips as MutableList).add(tooltip)
	}
}
