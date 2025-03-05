package al132.alchemistry.compat.jei.liquifier

import al132.alchemistry.client.GuiLiquifier
import al132.alchemistry.compat.jei.AlchemistryRecipeCategory
import al132.alchemistry.compat.jei.AlchemistryRecipeUID
import al132.alib.utils.Translator
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class LiquifierRecipeCategory(guiHelper: IGuiHelper)
    : AlchemistryRecipeCategory<LiquifierRecipeWrapper>(guiHelper.createDrawable(guiTexture, u, v, 98, 80),
        "jei.liquifier.name") {

    override fun getTitle(): String = Translator.translateToLocal("jei.liquifier.name")

    override fun getUid(): String = AlchemistryRecipeUID.LIQUIFIER

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: LiquifierRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        val guiFluidStacks = recipeLayout.fluidStacks


        var x = 44 - u
        var y = 117 - v
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, ingredients.getInputs(VanillaTypes.ITEM)[0])

        x = 116 - u
        y = 64 - v
        val outputFluidStack = ingredients.getOutputs(VanillaTypes.FLUID)[0][0]
        guiFluidStacks.init(FLUID_ONE, true, x, y, 16, 70, outputFluidStack.amount, false, null)
        guiFluidStacks.set(FLUID_ONE, outputFluidStack)
    }

    companion object {

        private val INPUT_ONE = 0
        private val FLUID_ONE = 1

        private val u = 39
        private val v = 59

        private val guiTexture = GuiLiquifier.textureLocation
    }
}