package io.enderdev.alchemistry.utils.extensions

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

fun ItemStack.areStacksEqualIgnoreQuantity(other: ItemStack): Boolean {
	return item == other.item
			&& metadata == other.metadata
			&& ItemStack.areItemStackTagsEqual(this, other)
}

fun ItemStack.canMergeWith(target: ItemStack, allowEmpty: Boolean): Boolean {
	return if(allowEmpty && (isEmpty || target.isEmpty))
		true
	else item === target.item && count + target.count <= maxStackSize && itemDamage == target.itemDamage && tagCompound === target.tagCompound
}

fun ItemStack.toIngredient(): Ingredient = Ingredient.fromStacks(this)

fun ItemStack.equalsIgnoreMeta(other: ItemStack): Boolean {
	return if(isEmpty && other.isEmpty) true
	else if(!isEmpty && !other.isEmpty) item === other.item
	else false
}
