package io.enderdev.alchemistry.recipes

import io.enderdev.alchemistry.recipes.register.DissolverRegister
import io.enderdev.alchemistry.utils.extensions.equalsIgnoreMeta
import io.enderdev.alchemistry.utils.extensions.toImmutable
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by al132 on 1/20/2017.
 */
data class DissolverRecipe(
	var input: Ingredient? = null,
	var reversible: Boolean = false,
	var internalOutputs: ProbabilitySet? = null
) : IRecipe {
	inline val inputs: List<ItemStack>
		get(): List<ItemStack> {
			val temp = ArrayList<ItemStack>()
			if(input != null) temp.addAll(input!!.matchingStacks.copyOf())
			return temp.toImmutable()
		}

	inline fun output(crossinline init: ProbabilitySetDSL.() -> Unit) {
		this.internalOutputs = ProbabilitySetDSL().apply { init() }.build()
	}

	inline val outputs: ProbabilitySet
		get() = internalOutputs!!.copy()

	companion object {
		fun match(input: ItemStack, quantitySensitive: Boolean): DissolverRecipe? {
			for(recipe in DissolverRegister.Companion.INSTANCE.recipes) {
				for(recipeStack in recipe.inputs) {
					if(recipeStack.equalsIgnoreMeta(input)
						&& (input.itemDamage == recipeStack.itemDamage
								|| recipeStack.itemDamage == OreDictionary.WILDCARD_VALUE)
					) {
						if(quantitySensitive && input.count >= recipeStack.count) return recipe.copy()
						else if(!quantitySensitive) return recipe.copy()
					}
				}
			}
			return null
		}
	}
}
