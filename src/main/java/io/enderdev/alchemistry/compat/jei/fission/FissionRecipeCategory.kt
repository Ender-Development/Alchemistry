package io.enderdev.alchemistry.compat.jei.fission

import io.enderdev.alchemistry.client.gui.GuiFissionController
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import al132.alib.utils.Translator
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class FissionRecipeCategory(guiHelper: IGuiHelper)
    : AlchemistryRecipeCategory<FissionRecipeWrapper>(guiHelper.createDrawable(guiTexture, u, v, 115, 26),
        "jei.fission_controller.name") {

    override fun getTitle(): String = Translator.translateToLocal("jei.fission_controller.name")

    override fun getUid(): String = AlchemistryRecipeUID.FISSION

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: FissionRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks

        var x = 43 - u
        var y = 74 - v
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

    companion object {

        private val INPUT_ONE = 0
        private val OUTPUT_ONE = 1
        private val OUTPUT_TWO = 2

        private val u = 39
        private val v = 70

        private val guiTexture = GuiFissionController.Companion.textureLocation
    }
}