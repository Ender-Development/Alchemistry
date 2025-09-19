package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.blocks.ModBlocks
import io.enderdev.alchemistry.recipes.CombinerRecipe
import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.utils.DevUtils
import org.ender_development.catalyx.utils.extensions.toStack

// note: this is a WIP item, the texture is wrong, the item id is weird and there's absolutely no translations (TODO)
// it's functional afaict, but yeah, not quite CF/MR-publishing ready.
class ItemCopyPasteTool : ItemBase("copy_paste_thingy_wip") {
	private companion object {
		const val NBT_KEY = "CopyPasteData"
		const val NBT_VALUE_EMPTY = ""
	}

	override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
		if(world.getBlockState(pos).block !== ModBlocks.chemical_combiner)
			return EnumActionResult.PASS

		val te = world.getTileEntity(pos) as? TileChemicalCombiner ?: return EnumActionResult.PASS
		val stack = player.getHeldItem(hand)
		val copy = player.isSneaking
		val tag = stack.tagCompound ?: NBTTagCompound()

		if(copy) {
			if(!te.recipeIsLocked || te.currentRecipe == null)
				tag.setString(NBT_KEY, NBT_VALUE_EMPTY)
			else
				tag.setString(NBT_KEY, te.currentRecipe!!.output.string())
		} else {
			val paste = tag.getString(NBT_KEY)
			if(paste == NBT_VALUE_EMPTY) {
				te.currentRecipe = null
				te.recipeIsLocked = false
				te.clientRecipeTarget.setStackInSlot(0, ItemStack.EMPTY)
			} else {
				val item = paste.stack()
				if(item == null) {
					player.sendMessage(TextComponentString("Unknown item: $paste")) // TODO better handling?
					return EnumActionResult.PASS
				}
				println(item)
				te.currentRecipe = CombinerRecipe.matchOutput(item)
				te.recipeIsLocked = te.currentRecipe != null
				if(te.recipeIsLocked)
					te.clientRecipeTarget.setStackInSlot(0, item.copy())
			}
		}
		if(!stack.hasTagCompound())
			stack.tagCompound = tag

		return EnumActionResult.SUCCESS
	}

	@SideOnly(Side.CLIENT)
	override fun addInformation(stack: ItemStack, playerIn: World?, tooltip: List<String>, advanced: ITooltipFlag) {
		tooltip as MutableList
		tooltip.add("Sneak+Right Click on a {Chemical Combiner} to copy its recipe")
		tooltip.add("Right Click on different {Chemical Combiner}s to set their recipes to what was copied")
		tooltip.add("Currently pasting: ${stack.tagCompound?.getString(NBT_KEY)?.let {
			// this is so cursed, I love Kotlin (sometimes.)
			if(DevUtils.isDeobfuscated)
				return@let "'$it'"

			if(it == NBT_VALUE_EMPTY)
				"nothing…"
			else
				it.stack()?.displayName
		} ?: "nothing…"}")
		tooltip.add("TODO TRANSLATION")
	}

	private fun ItemStack.string() =
		"${item.registryName}$${metadata}"

	private fun String.stack(): ItemStack? {
		val (name, meta) = split('$')
		return getByNameOrId(name)?.toStack(meta = meta.toInt())
	}
}
