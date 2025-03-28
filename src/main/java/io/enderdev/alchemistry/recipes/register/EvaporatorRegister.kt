package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.EvaporatorRecipe
import io.enderdev.alchemistry.utils.extensions.toStack
import net.minecraft.init.Blocks
import net.minecraftforge.fluids.FluidRegistry

class EvaporatorRegister : AbstractRecipeRegister<EvaporatorRecipe>() {
    companion object {
        val INSTANCE = EvaporatorRegister()
    }

    override fun registerRecipes() {
        recipes.add(EvaporatorRecipe(FluidRegistry.WATER, 125, ModItems.mineralSalt.toStack()))
        recipes.add(EvaporatorRecipe(FluidRegistry.LAVA, 1000, Blocks.MAGMA.toStack()))

        if (fluidExists("milk")) {
            recipes.add(EvaporatorRecipe(FluidRegistry.getFluid("milk"), 500, ModItems.condensedMilk.toStack()))
        }
    }
}