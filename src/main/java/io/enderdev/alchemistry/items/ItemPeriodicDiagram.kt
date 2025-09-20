package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.client.gui.GuiPeriodicTable
import net.minecraft.client.Minecraft
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.items.BaseItem
import org.ender_development.catalyx.utils.extensions.translate

class ItemPeriodicDiagram : BaseItem(Alchemistry.catalyxSettings, "periodic_table") {
	override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
		if(world.isRemote)
			Minecraft.getMinecraft().displayGuiScreen(GuiPeriodicTable())
		return ActionResult(EnumActionResult.PASS, player.getHeldItem(hand))
	}

	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack, playerIn: World?, tooltip: List<String>, advanced: ITooltipFlag) {
		(tooltip as MutableList).add("item.${Tags.MOD_ID}:periodic_table.tooltip".translate())
	}
}
