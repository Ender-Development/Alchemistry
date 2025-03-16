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
    override val v = 77
    override val width = 116
    override val height = 62

    override fun getUid(): String = AlchemistryRecipeUID.COMBINER

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: CombinerRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        val startX = 43 - u
        var x = startX
        var y = 81 - v
        var index = 0
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                guiItemStacks.init(index, true, x, y)
                index++
                x += 18
            }
            x = startX
            y += 18
        }


        for (i in 0 until INPUT_SIZE) {
            guiItemStacks.set(i, ingredients.getInputs(VanillaTypes.ITEM)[i])
        }

        x = 133 - u
        y = 99 - v

        guiItemStacks.init(OUTPUT_SLOT, false, x, y)
        //guiItemStacks.set(OUTPUT_SLOT, ingredients.getOutputs(ItemStack::class.java)[0])
        guiItemStacks.set(OUTPUT_SLOT, recipeWrapper.recipe.output)
    }
}