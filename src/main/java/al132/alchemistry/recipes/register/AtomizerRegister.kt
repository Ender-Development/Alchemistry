package al132.alchemistry.recipes.register

import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.recipes.AtomizerRecipe
import al132.alchemistry.utils.extensions.toStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

class AtomizerRegister : AbstractRecipeRegister<AtomizerRecipe>() {
    companion object {
        val INSTANCE = AtomizerRegister()
    }

    override fun registerRecipes() {
        recipes.add(AtomizerRecipe(true, FluidStack(FluidRegistry.WATER, 500), "water".toStack(8)))

        if (fluidExists("if.protein")) {
            recipes.add(AtomizerRecipe(true,
                    FluidRegistry.getFluidStack("if.protein", 500)!!, "protein".toStack(8)))
        }
        if (fluidExists("canolaoil")) {
            recipes.add(AtomizerRecipe(true,
                    FluidRegistry.getFluidStack("canolaoil", 500)!!, "triglyceride".toStack(4)))
        }
        if (fluidExists("cocoa_butter")) {
            recipes.add(AtomizerRecipe(true,
                    FluidRegistry.getFluidStack("cocoa_butter", 144)!!, "triglyceride".toStack(1)))
        }
        if (fluidExists("ethanol")) {
            recipes.add(AtomizerRecipe(true,
                    FluidRegistry.getFluidStack("ethanol", 500)!!, "ethanol".toStack(8)))
        }

        ElementRegistry.getAllElements().forEach {
            if (fluidExists(it.name)) {
                recipes.add(AtomizerRecipe(true,
                        FluidRegistry.getFluidStack(it.name, 144)!!, it.name.toStack(16)))
            }
        }
    }
}