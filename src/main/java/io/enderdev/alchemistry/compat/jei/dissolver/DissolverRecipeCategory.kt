package io.enderdev.alchemistry.compat.jei.dissolver

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeCategory
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeUID
import mezz.jei.api.IGuiHelper
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class DissolverRecipeCategory(guiHelper: IGuiHelper) : AlchemistryRecipeCategory<DissolverRecipeWrapper>(guiHelper, "chemical_dissolver") {
    companion object {
        private const val INPUT_ONE = 2
        private const val OUTPUT_STARTING_INDEX = 3
    }

    override val guiTexture = ResourceLocation(Tags.MOD_ID, "textures/gui/container/chemical_dissolver_jei_redox.png")
    override val u = 5
    override val v = 5
    override val width = 180
    override val height = 256

    override fun getUid(): String = AlchemistryRecipeUID.DISSOLVER

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: DissolverRecipeWrapper, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        val inputStack: List<ItemStack> = recipeWrapper.recipe.inputs
        val outputSet = recipeWrapper.recipe.outputs.set
        var x = 99 - u
        var y = 14 - v
        guiItemStacks.init(INPUT_ONE, true, x, y)
        guiItemStacks.set(INPUT_ONE, inputStack)
        x = 45 - u
        y = 50 - v

        var outputSlotIndex = OUTPUT_STARTING_INDEX
        for (component in outputSet) {
            for (stack in component.output) {
                guiItemStacks.init(outputSlotIndex, false, x, y)
                guiItemStacks.set(outputSlotIndex, stack)
                x += 18
                outputSlotIndex++
            }
            x = 45 - u
            y += 18
        }
    }
}