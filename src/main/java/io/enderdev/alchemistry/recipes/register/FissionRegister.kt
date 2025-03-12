package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.recipes.FissionRecipe

class FissionRegister: AbstractRecipeRegister<FissionRecipe>() {
    companion object {
        val INSTANCE = FissionRegister()
    }

    override fun registerRecipes() {
        for (i in ElementRegistry.keys().filterNot { it == 1 }) {
            val output1 = if (i % 2 == 0) i / 2 else (i / 2) + 1
            val output2 = if (i % 2 == 0) 0 else i / 2
            if (ElementRegistry[output1] != null && (output2 == 0 || ElementRegistry[output2] != null)) {
                recipes.add(FissionRecipe(i, output1, output2))
            }
        }
    }
}