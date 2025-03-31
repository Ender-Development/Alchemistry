package io.enderdev.alchemistry.compat.groovyscript.register

import com.cleanroommc.groovyscript.api.GroovyBlacklist
import com.cleanroommc.groovyscript.api.GroovyLog
import com.cleanroommc.groovyscript.api.IIngredient
import com.cleanroommc.groovyscript.api.documentation.annotations.*
import com.cleanroommc.groovyscript.helper.SimpleObjectStream
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.compat.groovyscript.GSPlugin
import io.enderdev.alchemistry.recipes.EvaporatorRecipe
import io.enderdev.alchemistry.recipes.register.EvaporatorRegister
import org.jetbrains.annotations.Nullable

@RegistryDescription(linkGenerator = Tags.MOD_ID)
class Evaporator : VirtualizedRegistry<EvaporatorRecipe>() {
	@GroovyBlacklist
	override fun onReload() {
		EvaporatorRegister.INSTANCE.recipes.removeAll(removeScripted())
		EvaporatorRegister.INSTANCE.recipes.addAll(restoreFromBackup())
	}

	@MethodDescription(type = MethodDescription.Type.ADDITION)
	fun add(recipe: EvaporatorRecipe?) {
		recipe?.let {
			addScripted(recipe)
			EvaporatorRegister.INSTANCE.recipes.add(recipe)
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL)
	fun remove(recipe: EvaporatorRecipe?): Boolean {
		if(EvaporatorRegister.INSTANCE.recipes.removeIf { r -> r == recipe }) {
			addBackup(recipe)
			return true
		}
		return false
	}

	@MethodDescription(
		type = MethodDescription.Type.REMOVAL,
		example = [Example(value = "fluid('water')", commented = true)]
	)
	fun removeByInput(input: IIngredient): Boolean {
		return EvaporatorRegister.INSTANCE.recipes.removeIf { r ->
			if(r.input.equals(input)) {
				addBackup(r)
				return@removeIf true
			}
			return@removeIf false
		}
	}

	@MethodDescription(
		type = MethodDescription.Type.REMOVAL,
		example = [Example(value = "item('alchemistry:mineral_salt')", commented = true)]
	)
	fun removeByOutput(output: IIngredient): Boolean {
		return EvaporatorRegister.INSTANCE.recipes.removeIf { r ->
			if(r.output.equals(output)) {
				addBackup(r)
				return@removeIf true
			}
			return@removeIf false
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL, priority = 2000, example = [Example(commented = true)])
	fun removeAll() {
		EvaporatorRegister.INSTANCE.recipes.forEach { this::addBackup }
		EvaporatorRegister.INSTANCE.recipes.clear()
	}

	@MethodDescription(type = MethodDescription.Type.QUERY)
	fun streamRecipes(): SimpleObjectStream<EvaporatorRecipe> {
		return SimpleObjectStream(EvaporatorRegister.INSTANCE.recipes).setRemover { r -> remove(r) }
	}

	@RecipeBuilderDescription(
		example = [
			Example(".fluidInput(fluid('water') * 10).output(item('minecraft:clay') * 4)"),
			Example(".fluidInput(fluid('lava') * 100).output(item('minecraft:redstone'))")
		]
	)
	fun recipeBuilder(): RecipeBuilder {
		return RecipeBuilder()
	}

	@Property(property = "fluidInput", comp = Comp(eq = 1))
	@Property(property = "output", comp = Comp(eq = 1))
	class RecipeBuilder : AbstractRecipeBuilder<EvaporatorRecipe>() {

		override fun getErrorMsg(): String? {
			return "An error occurred while building an Alchemistry Evaporator recipe."
		}

		override fun validate(msg: GroovyLog.Msg?) {
			validateItems(msg, 0, 0, 1, 1)
			validateFluids(msg, 1, 1, 0, 0)
		}

		@Nullable
		@RecipeBuilderRegistrationMethod
		override fun register(): EvaporatorRecipe? {
			if(!validate()) return null
			val recipe = EvaporatorRecipe(fluidInput[0], output[0])
			GSPlugin.Companion.instance?.evaporator?.add(recipe)
			return recipe
		}
	}
}
