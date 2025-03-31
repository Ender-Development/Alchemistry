package io.enderdev.alchemistry.compat.jei.atomizer

import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeWrapper
import io.enderdev.alchemistry.recipes.AtomizerRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class AtomizerRecipeWrapper(recipe: AtomizerRecipe) : AlchemistryRecipeWrapper<AtomizerRecipe>(recipe) {

	override fun getIngredients(ingredients: IIngredients) {
		ingredients.setInput(VanillaTypes.FLUID, recipe.input)
		ingredients.setOutput(VanillaTypes.ITEM, recipe.output)
	}
}
