package io.enderdev.alchemistry.compat.jei.fusion

import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeWrapper
import io.enderdev.alchemistry.recipes.FusionRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import net.minecraft.item.ItemStack

class FusionRecipeWrapper(recipe: FusionRecipe) : AlchemistryRecipeWrapper<FusionRecipe>(recipe) {

    override fun getIngredients(ingredients: IIngredients) {
        val input1 = ElementRegistry[recipe.inputMeta1]?.toItemStack(1)
        var input2 = ElementRegistry[recipe.inputMeta2]?.toItemStack(1)
        var output1 = ElementRegistry[recipe.outputMeta]?.toItemStack(1)
        ingredients.setInputs(VanillaTypes.ITEM, listOf(input1, input2))
        ingredients.setOutput(VanillaTypes.ITEM, output1 ?: ItemStack.EMPTY)
    }
}