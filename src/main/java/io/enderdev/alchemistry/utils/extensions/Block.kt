package io.enderdev.alchemistry.utils.extensions

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

fun Block.toStack(quantity: Int = 1, meta: Int = 0) = ItemStack(this, quantity, meta)

fun Block.toIngredient(quantity: Int = 1, meta: Int = 0): Ingredient = Ingredient.fromStacks(toStack(quantity, meta))
