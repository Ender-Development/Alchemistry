package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.recipes.LiquifierRecipe

class LiquifierRegister: AbstractRecipeRegister<LiquifierRecipe>() {
    companion object {
        val INSTANCE = LiquifierRegister()
    }

    override fun registerRecipes() {
        AtomizerRegister.INSTANCE.recipes.filter { it.reversible }.forEach {
            recipes.add(LiquifierRecipe(it.output.copy(), it.input.copy()))
        }
    }
}