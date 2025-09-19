package io.enderdev.alchemistry.recipes

import io.enderdev.alchemistry.recipes.register.CombinerRegister
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.oredict.OreDictionary
import org.ender_development.catalyx.tiles.helper.TileStackHandler
import org.ender_development.catalyx.utils.extensions.equalsIgnoreMeta
import org.ender_development.catalyx.utils.extensions.get
import org.ender_development.catalyx.utils.extensions.toStackList

data class CombinerRecipe(val output: ItemStack, private val objsIn: List<Any?>, var gamestage: String = "") : IRecipe {
	val inputs = ArrayList<ItemStack>()

	init {
		val tempInputs = objsIn
		(0..<INPUT_COUNT).forEach { index ->
			val tempInput = tempInputs.getOrNull(index)
			when(tempInput) {
				is ItemStack -> inputs.add(tempInput)
				is Item -> inputs.add(ItemStack(tempInput))
				is Block -> inputs.add(ItemStack(tempInput))
				else -> inputs.add(ItemStack.EMPTY)
			}
		}
	}

	fun matchesHandlerStacks(handler: TileStackHandler): Boolean {
		var matchingStacks = 0

		for((index: Int, recipeStack: ItemStack) in this.inputs.withIndex()) {
			val handlerStack = handler[index]
			if(handlerStack.isEmpty && recipeStack.isEmpty) matchingStacks++
			else if(handlerStack.isEmpty || recipeStack.isEmpty) continue
			else if(handlerStack.equalsIgnoreMeta(recipeStack)
				&& handlerStack.count >= recipeStack.count
				&& (handlerStack.itemDamage == recipeStack.itemDamage
						|| recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)
			) {
				matchingStacks++
			}
		}
		return (matchingStacks == INPUT_COUNT)
	}

	companion object {

		private const val INPUT_COUNT = 9

		fun matchInputs(handler: IItemHandler): CombinerRecipe? {
			assert(handler.slots == INPUT_COUNT)
			return matchInputs(handler.toStackList())
		}

		private fun matchInputs(inputStacks: List<ItemStack>): CombinerRecipe? {
			outer@ for(recipe in CombinerRegister.Companion.INSTANCE.recipes) {
				inner@ for((index: Int, recipeStack: ItemStack) in recipe.inputs.withIndex()) {
					val inputStack: ItemStack = inputStacks[index]
					if(inputStack.isEmpty && recipeStack.isEmpty) {
						continue@inner
					} else if(!(inputStack.equalsIgnoreMeta(recipeStack)
								&& inputStack.count >= recipeStack.count
								&& (inputStack.itemDamage == recipeStack.itemDamage || recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE))
					) {
						continue@outer
					} else if(inputStack.isEmpty || recipeStack.isEmpty) {
						continue@outer
					}
				}
				return recipe
			}
			return null
		}

		fun matchOutput(stack: ItemStack): CombinerRecipe? {
			return CombinerRegister.Companion.INSTANCE.recipes
				.filter { it.output.item == stack.item }
				.firstOrNull { ItemStack.areItemStacksEqual(it.output, stack) }
		}
	}
}
