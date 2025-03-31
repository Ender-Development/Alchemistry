package io.enderdev.alchemistry.compat.jei.evaporator

import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class EvaporatorRecipeCategory(guiHelper: IGuiHelper) : AlchemistryRecipeCategory<EvaporatorRecipeWrapper>(guiHelper, "evaporator") {
	companion object {
		private const val OUTPUT_ONE = 1
		private const val FLUID_ONE = 1
	}

	override val u = 39
	override val v = 16
	override val width = 98
	override val height = 80

	override fun getUid(): String = AlchemistryRecipeUID.EVAPORATOR

	override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: EvaporatorRecipeWrapper, ingredients: IIngredients) {
		val guiItemStacks = recipeLayout.itemStacks
		val guiFluidStacks = recipeLayout.fluidStacks

		var x = 115 - u
		var y = 74 - v
		guiItemStacks.init(OUTPUT_ONE, false, x, y)
		guiItemStacks.set(OUTPUT_ONE, ingredients.getOutputs(VanillaTypes.ITEM)[0])

		x = 44 - u
		y = 44 - u
		val inputStack = ingredients.getInputs(VanillaTypes.FLUID)[0][0]
		guiFluidStacks.init(FLUID_ONE, true, x, y, 16, 70, inputStack.amount, false, null)
		guiFluidStacks.set(FLUID_ONE, inputStack)
	}
}
