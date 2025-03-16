package io.enderdev.alchemistry.compat.jei.liquifier

import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class LiquifierRecipeCategory(guiHelper: IGuiHelper) : AlchemistryRecipeCategory<LiquifierRecipeWrapper>(guiHelper, "liquifier") {
    companion object {
        private const val INPUT_ONE = 0
        private const val FLUID_ONE = 1
    }

    override val u = 39
    override val v = 59
    override val width = 98
    override val height = 80

    override fun getUid(): String = AlchemistryRecipeUID.LIQUIFIER

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: LiquifierRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        val guiFluidStacks = recipeLayout.fluidStacks


        var x = 43 - u
        var y = 117 - v
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, ingredients.getInputs(VanillaTypes.ITEM)[0])

        x = 116 - u
        y = 64 - v
        val outputFluidStack = ingredients.getOutputs(VanillaTypes.FLUID)[0][0]
        guiFluidStacks.init(FLUID_ONE, true, x, y, 16, 70, outputFluidStack.amount, false, null)
        guiFluidStacks.set(FLUID_ONE, outputFluidStack)
    }
}