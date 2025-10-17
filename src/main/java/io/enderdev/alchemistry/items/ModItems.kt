package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.Tags
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.items.BaseItem
import org.ender_development.catalyx.utils.extensions.translate

object ModItems {
	val mineralSalt = BaseItem(Alchemistry, "mineral_salt")
	val condensedMilk = BaseItem(Alchemistry, "condensed_milk")
	val fertilizer = ItemFertilizer()
	val obsidianBreaker = object : BaseItem(Alchemistry, "obsidian_breaker") {
		override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
			tooltip.add("item.${Tags.MOD_ID}:obsidian_breaker.tooltip".translate())
		}
	}

	val elements = ItemElement("element")
	val compounds = ItemCompound("compound")
	val ingots = ItemElementIngot("ingot")
	val periodicDiagram = ItemPeriodicDiagram()

	@SideOnly(Side.CLIENT)
	fun initColors() =
		Minecraft.getMinecraft().itemColors.registerItemColorHandler(ItemColorHandler(), compounds, ingots, elements)

	fun nya() {}
}
