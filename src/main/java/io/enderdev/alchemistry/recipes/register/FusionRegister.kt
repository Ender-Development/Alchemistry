package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.recipes.FusionRecipe

class FusionRegister : AbstractRecipeRegister<FusionRecipe>() {
    companion object {
        val INSTANCE = FusionRegister()
    }

    override fun registerRecipes() {
        ElementRegistry.getAllElements().forEach { element1 ->
            ElementRegistry.getAllElements().forEach { element2 ->
                // if (recipes.any { it.inputMeta1 == element2.meta && it.inputMeta2 == element1.meta }) return@forEach
                if (ElementRegistry[element1.meta + element2.meta] != null) {
                    recipes.add(FusionRecipe(element1.meta, element2.meta))
                }
            }
        }
    }
}