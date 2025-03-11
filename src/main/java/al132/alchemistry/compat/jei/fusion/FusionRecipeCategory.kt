package al132.alchemistry.compat.jei.fusion

import al132.alchemistry.client.GuiFusionController
import al132.alchemistry.compat.jei.AlchemistryRecipeCategory
import al132.alchemistry.compat.jei.AlchemistryRecipeUID
import al132.alib.utils.Translator
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes

class FusionRecipeCategory(guiHelper: IGuiHelper)
    : AlchemistryRecipeCategory<FusionRecipeWrapper>(guiHelper.createDrawable(guiTexture, u, v, 115, 26),
        "jei.fusion_controller.name") {

    override fun getTitle(): String = Translator.translateToLocal("jei.fission_controller.name")

    override fun getUid(): String = AlchemistryRecipeUID.FUSION

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: FusionRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks

        var x = 43 - u
        var y = 74 - v

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

    companion object {

        private val INPUT_ONE = 0
        private val INPUT_TWO = 1
        private val OUTPUT_ONE = 2

        private val u = 39
        private val v = 70

        private val guiTexture = GuiFusionController.textureLocation
    }
}