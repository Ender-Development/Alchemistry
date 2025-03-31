package io.enderdev.alchemistry.compat.jei.combiner

import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class CombinerRecipeCategory(guiHelper: IGuiHelper) : AlchemistryRecipeCategory<CombinerRecipeWrapper>(guiHelper, "chemical_combiner") {
	companion object {
		private const val INPUT_SIZE = 9
		private const val OUTPUT_SLOT = 9
	}

	override val u = 39
	override val v = 34
	override val width = 116
	override val height = 62

	override fun getUid(): String = AlchemistryRecipeUID.COMBINER

	override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: CombinerRecipeWrapper, ingredients: IIngredients) {
		val guiItemStacks = recipeLayout.itemStacks
		var x = 43 - u
		var y = 38 - v
		var index = 0
		for(row in 0..2)
			for(col in 0..2) {
				guiItemStacks.init(index, true, x + 18 * col, y + 18 * row)
				guiItemStacks.set(index, ingredients.getInputs(VanillaTypes.ITEM)[index++])
			}

		x = 133 - u
		y = 56 - v

		guiItemStacks.init(OUTPUT_SLOT, false, x, y)
		guiItemStacks.set(OUTPUT_SLOT, recipeWrapper.recipe.output)
	}
}
