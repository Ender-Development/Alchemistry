package io.enderdev.alchemistry.recipes

import io.enderdev.alchemistry.recipes.register.DissolverRegister
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.oredict.OreDictionary
import org.ender_development.catalyx.utils.extensions.equalsIgnoreMeta
import org.ender_development.catalyx.utils.extensions.toImmutable

data class DissolverRecipe(
	var input: Ingredient? = null,
	var reversible: Boolean = false,
	var internalOutputs: ProbabilitySet? = null
) : IRecipe {
	inline val inputs: List<ItemStack>
		get(): List<ItemStack> {
			val temp = ArrayList<ItemStack>()
			if(input != null) temp.addAll(input!!.getMatchingStacks().copyOf())
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
