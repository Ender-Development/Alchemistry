package io.enderdev.alchemistry.compat.jei.combiner

import al132.alib.utils.Translator
import io.enderdev.alchemistry.client.gui.GuiChemicalCombiner
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class CombinerRecipeCategory(guiHelper: IGuiHelper) :
        AlchemistryRecipeCategory<CombinerRecipeWrapper>
        (guiHelper.createDrawable(guiTexture, u, v, 116, 62), "jei.combiner.name") {

    companion object {

        private const val INPUT_SIZE = 9
        private const val OUTPUT_SLOT = 9

        private const val u = 39
        private const val v = 77

        private val guiTexture = GuiChemicalCombiner.Companion.textureLocation
    }

    override fun getTitle() = Translator.translateToLocal("jei.combiner.name")

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