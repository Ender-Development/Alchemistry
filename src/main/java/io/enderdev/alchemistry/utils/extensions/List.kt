package io.enderdev.alchemistry.utils.extensions

import com.google.common.collect.ImmutableList
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

fun List<ItemStack>.containsItem(stack: ItemStack, strict: Boolean = false): Boolean =
	any { OreDictionary.itemMatches(it, stack, strict) }

fun <T> List<T>.toImmutable(): ImmutableList<T> = ImmutableList.copyOf(this)
