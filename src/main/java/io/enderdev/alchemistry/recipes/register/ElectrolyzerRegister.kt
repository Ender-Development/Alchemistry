package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.recipes.ElectrolyzerRecipe
import io.enderdev.alchemistry.utils.extensions.toIngredient
import io.enderdev.alchemistry.utils.extensions.toStack
import net.minecraftforge.fluids.FluidRegistry

class ElectrolyzerRegister : AbstractRecipeRegister<ElectrolyzerRecipe>() {
    companion object {
        val INSTANCE = ElectrolyzerRegister()
    }

    override fun registerRecipes() {
        recipes.add(
            ElectrolyzerRecipe(
                FluidRegistry.WATER.toStack(125),
                "calcium_carbonate".toIngredient(),
                20,
                "hydrogen".toStack(4),
                "oxygen".toStack(2)
            )
        )

        recipes.add(
            ElectrolyzerRecipe(
                FluidRegistry.WATER.toStack(125),
                "sodium_chloride".toIngredient(),
                20,
                "hydrogen".toStack(2),
                "oxygen".toStack(1),
                "chlorine".toStack(2),
                10
            )
        )
    }
}