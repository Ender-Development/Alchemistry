package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.recipes.AtomizerRecipe
import io.enderdev.alchemistry.utils.extensions.chemical
import io.enderdev.catalyx.utils.extensions.toStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack

class AtomizerRegister : AbstractRecipeRegister<AtomizerRecipe>() {
	companion object {
		val INSTANCE = AtomizerRegister()
	}

	override fun registerRecipes() {
		recipes.add(AtomizerRecipe(true, FluidStack(FluidRegistry.WATER, 500), "water".chemical(8)))

		if(fluidExists("if.protein")) {
			recipes.add(
				AtomizerRecipe(
					true,
					FluidRegistry.getFluidStack("if.protein", 500)!!, "protein".chemical(8)
				)
			)
		}
		if(fluidExists("canolaoil")) {
			recipes.add(
				AtomizerRecipe(
					true,
					FluidRegistry.getFluidStack("canolaoil", 500)!!, "triglyceride".chemical(4)
				)
			)
		}
		if(fluidExists("cocoa_butter")) {
			recipes.add(
				AtomizerRecipe(
					true,
					FluidRegistry.getFluidStack("cocoa_butter", 144)!!, "triglyceride".chemical(1)
				)
			)
		}
		if(fluidExists("ethanol")) {
			recipes.add(
				AtomizerRecipe(
					true,
					FluidRegistry.getFluidStack("ethanol", 500)!!, "ethanol".chemical(8)
				)
			)
		}

		ElementRegistry.getAllElements().forEach {
			if(fluidExists(it.name)) {
				recipes.add(
					AtomizerRecipe(
						true,
						FluidRegistry.getFluidStack(it.name, 144)!!, it.name.toStack(16)
					)
				)
			}
		}
	}
}
