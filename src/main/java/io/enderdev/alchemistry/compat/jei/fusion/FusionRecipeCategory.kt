package io.enderdev.alchemistry.compat.jei.fusion

import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class FusionRecipeCategory(guiHelper: IGuiHelper) : AlchemistryRecipeCategory<FusionRecipeWrapper>(guiHelper,"fusion_controller") {
    companion object {
        private const val INPUT_ONE = 0
        private const val INPUT_TWO = 1
        private const val OUTPUT_ONE = 2
    }

    override val u = 39
    override val v = 70
    override val width = 115
    override val height = 26

    override fun getUid(): String = AlchemistryRecipeUID.FUSION

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: FusionRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks

        var x = 43 - u
        val y = 74 - v

        val input1 = ingredients.getInputs(VanillaTypes.ITEM)[0]
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, input1)

        x += 18
        val input2 = ingredients.getInputs(VanillaTypes.ITEM)[1]
        guiItemStacks.init(INPUT_TWO, false, x, y)
        guiItemStacks.set(INPUT_TWO, input2)

        x = 133 - u
        val output1 = ingredients.getOutputs(VanillaTypes.ITEM)[0]
        guiItemStacks.init(OUTPUT_ONE, false, x, y)
        guiItemStacks.set(OUTPUT_ONE, output1)
    }
}