package io.enderdev.alchemistry.utils.extensions

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

fun ItemStack.areStacksEqualIgnoreQuantity(other: ItemStack): Boolean {
	return this.item == other.item
			&& this.metadata == other.metadata
			&& ItemStack.areItemStackTagsEqual(this, other)
}

fun ItemStack.canMergeWith(target: ItemStack, allowEmpty: Boolean): Boolean {
	if(allowEmpty && (isEmpty || target.isEmpty))
		return true
	return item === target.item && count + target.count <= maxStackSize && itemDamage == target.itemDamage && tagCompound === target.tagCompound
}

fun ItemStack.toIngredient(quantity: Int = 1, meta: Int = 0): Ingredient = Ingredient.fromStacks(this)

fun ItemStack.equalsIgnoreMeta(other: ItemStack): Boolean {
	return if(isEmpty && other.isEmpty) true
	else if(!isEmpty && !other.isEmpty) return item === other.item
	else false
}