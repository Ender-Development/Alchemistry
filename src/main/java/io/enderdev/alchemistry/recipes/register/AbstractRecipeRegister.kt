package io.enderdev.alchemistry.recipes.register

import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.recipes.IRecipe
import io.enderdev.alchemistry.utils.extensions.toImmutable
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.oredict.OreDictionary

abstract class AbstractRecipeRegister<T : IRecipe> {
	val recipes: MutableList<T> = mutableListOf()

	val heathens: Map<String, String> = mapOf(
		"aluminium" to "aluminum",
		"caesium" to "cesium"
	)

	val metals: List<String> = mutableListOf<String>()
		.apply { addAll(heathens.keys) }
		.apply { addAll(ElementRegistry.getAllElements().map { it.name }) }
		.toImmutable()

	abstract fun registerRecipes()

	fun fluidExists(name: String): Boolean = FluidRegistry.isFluidRegistered(name)

	fun oreNotEmpty(ore: String) = OreDictionary.doesOreNameExist(ore) && OreDictionary.getOres(ore).isNotEmpty()
}
