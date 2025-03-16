package io.enderdev.alchemistry.compat.jei.fission

import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class FissionRecipeCategory(guiHelper: IGuiHelper) : AlchemistryRecipeCategory<FissionRecipeWrapper>(guiHelper,"fission_controller") {
    companion object {
        private const val INPUT_ONE = 0
        private const val OUTPUT_ONE = 1
        private const val OUTPUT_TWO = 2
    }

    override val u = 39
    override val v = 70
    override val width = 115
    override val height = 26

    override fun getUid(): String = AlchemistryRecipeUID.FISSION

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: FissionRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks

        var x = 43 - u
        val y = 74 - v
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, ingredients.getInputs(VanillaTypes.ITEM)[0])

        x = 115 - u
        val output1 = ingredients.getOutputs(VanillaTypes.ITEM)[0]
        val output2 = ingredients.getOutputs(VanillaTypes.ITEM)[1]
        guiItemStacks.init(OUTPUT_ONE, false, x, y)
        guiItemStacks.set(OUTPUT_ONE, output1)
        x += 18
        guiItemStacks.init(OUTPUT_TWO, false, x, y)
        guiItemStacks.set(OUTPUT_TWO, output2)
    }
}