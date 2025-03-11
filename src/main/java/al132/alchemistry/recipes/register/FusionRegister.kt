package al132.alchemistry.recipes.register

import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.recipes.FusionRecipe

class FusionRegister: AbstractRecipeRegister<FusionRecipe>() {
    companion object {
        val INSTANCE = FusionRegister()
    }

    override fun registerRecipes() {
        ElementRegistry.getAllElements().forEach { element1 ->
            ElementRegistry.getAllElements().forEach { element2 ->
                if (element1.meta + element2.meta <= ElementRegistry.keys().size)
                    recipes.add(FusionRecipe(element1.meta, element2.meta))
            }
        }
    }
}