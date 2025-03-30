package io.enderdev.alchemistry.compat.jei.electrolyzer

import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import io.enderdev.alchemistry.utils.extensions.translate
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class ElectrolyzerRecipeCategory(guiHelper: IGuiHelper) : AlchemistryRecipeCategory<ElectrolyzerRecipeWrapper>(guiHelper, "electrolyzer") {
    companion object {
        private const val INPUT_ONE = 0
        private const val OUTPUT_ONE = 1
        private const val OUTPUT_TWO = 2
        private const val OUTPUT_THREE = 3
        private const val OUTPUT_FOUR = 4
        private const val FLUID_ONE = 1
    }

    override val u = 39
    override val v = 16
    override val width = 116
    override val height = 80

    override fun getUid(): String = AlchemistryRecipeUID.ELECTROLYZER

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: ElectrolyzerRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        val guiFluidStacks = recipeLayout.fluidStacks


        var x = 79 - u
        var y = 38 - v
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, recipeWrapper.recipe.electrolytes)

        x = 115 - u
        y = 56 - v
        guiItemStacks.init(OUTPUT_ONE, false, x, y)
        x += 18
        guiItemStacks.init(OUTPUT_THREE, false, x, y)
        x -= 18
        y += 18
        guiItemStacks.init(OUTPUT_TWO, false, x, y)
        x += 18
        guiItemStacks.init(OUTPUT_FOUR, false, x, y)

        guiItemStacks.set(OUTPUT_ONE, ingredients.getOutputs(VanillaTypes.ITEM)[0])
        guiItemStacks.set(OUTPUT_TWO, ingredients.getOutputs(VanillaTypes.ITEM)[1])
        guiItemStacks.set(OUTPUT_THREE, ingredients.getOutputs(VanillaTypes.ITEM)[2])
        guiItemStacks.set(OUTPUT_FOUR, ingredients.getOutputs(VanillaTypes.ITEM)[3])


        x = 44 - u
        y = 44 - u
        val inputStack = ingredients.getInputs(VanillaTypes.FLUID)[0][0]
        guiFluidStacks.init(FLUID_ONE, true, x, y, 16, 70, inputStack.amount, false, null)
        guiFluidStacks.set(FLUID_ONE, inputStack)

        guiItemStacks.addTooltipCallback { slotIndex, input, ingredient, tooltip ->
            if(input) {
                tooltip.add("jei.electrolyzer.electrolyte".translate())
                tooltip.add("jei.electrolyzer.consumption_probability".translate(recipeWrapper.recipe.electrolyteConsumptionChance))
            }
        }
    }
}