package al132.alchemistry.recipes.register

import al132.alchemistry.recipes.ElectrolyzerRecipe
import al132.alchemistry.utils.extensions.toStack
import al132.alib.utils.extensions.toIngredient
import al132.alib.utils.extensions.toStack
import net.minecraftforge.fluids.FluidRegistry

class ElectrolyzerRegister : AbstractRecipeRegister<ElectrolyzerRecipe>() {
    companion object {
        val INSTANCE = ElectrolyzerRegister()
    }

    override fun registerRecipes() {
        recipes.add(
            ElectrolyzerRecipe(
                FluidRegistry.WATER.toStack(125),
                "calcium_carbonate".toStack().toIngredient(),
                20,
                "hydrogen".toStack(4),
                "oxygen".toStack(2)
            )
        )

        recipes.add(
            ElectrolyzerRecipe(
                FluidRegistry.WATER.toStack(125),
                "sodium_chloride".toStack().toIngredient(),
                20,
                "hydrogen".toStack(2),
                "oxygen".toStack(1),
                "chlorine".toStack(2),
                10
            )
        )
    }
}