package io.enderdev.alchemistry.compat.jei.atomizer

import io.enderdev.alchemistry.client.gui.GuiAtomizer
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import al132.alib.utils.Translator
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class AtomizerRecipeCategory(guiHelper: IGuiHelper)
    : AlchemistryRecipeCategory<AtomizerRecipeWrapper>(guiHelper.createDrawable(guiTexture, u, v, 98, 80),
        "jei.atomizer.name") {

    override fun getTitle(): String = Translator.translateToLocal("jei.atomizer.name")

    override fun getUid(): String = AlchemistryRecipeUID.ATOMIZER

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: AtomizerRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        val guiFluidStacks = recipeLayout.fluidStacks


        var x = 115 - u
        var y = 117 - v
        guiItemStacks.init(OUTPUT_ONE, false, x, y)
        guiItemStacks.set(OUTPUT_ONE, ingredients.getOutputs(VanillaTypes.ITEM)[0])

        x = 44 - u
        y = 44 - u
        val inputStack = ingredients.getInputs(VanillaTypes.FLUID)[0][0]
        guiFluidStacks.init(FLUID_ONE, true, x, y, 16, 70, inputStack.amount, false, null)
        guiFluidStacks.set(FLUID_ONE, inputStack)
    }

    companion object {

        private val OUTPUT_ONE = 1
        private val FLUID_ONE = 1

        private val u = 39
        private val v = 59

        private val guiTexture = GuiAtomizer.Companion.textureLocation
    }
}