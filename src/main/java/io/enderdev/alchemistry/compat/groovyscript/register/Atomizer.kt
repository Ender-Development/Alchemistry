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
import io.enderdev.alchemistry.recipes.AtomizerRecipe
import io.enderdev.alchemistry.recipes.LiquifierRecipe
import io.enderdev.alchemistry.recipes.register.AtomizerRegister
import org.jetbrains.annotations.Nullable

@RegistryDescription(linkGenerator = Tags.MOD_ID)
class Atomizer : VirtualizedRegistry<AtomizerRecipe>() {

	@GroovyBlacklist
	override fun onReload() {
		AtomizerRegister.INSTANCE.recipes.removeAll(removeScripted())
		AtomizerRegister.INSTANCE.recipes.addAll(restoreFromBackup())
	}

	@MethodDescription(type = MethodDescription.Type.ADDITION)
	fun add(recipe: AtomizerRecipe?) {
		recipe?.let {
			addScripted(recipe)
			AtomizerRegister.INSTANCE.recipes.add(recipe)
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL)
	fun remove(recipe: AtomizerRecipe?): Boolean {
		if(AtomizerRegister.INSTANCE.recipes.removeIf { r -> r == recipe }) {
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
		return AtomizerRegister.INSTANCE.recipes.removeIf { r ->
			if(r.input.equals(input)) {
				addBackup(r)
				return@removeIf true
			}
			return@removeIf false
		}
	}

	@MethodDescription(
		type = MethodDescription.Type.REMOVAL,
		example = [Example(value = "item('alchemistry:compound:7')", commented = true)]
	)
	fun removeByOutput(output: IIngredient): Boolean {
		return AtomizerRegister.INSTANCE.recipes.removeIf { r ->
			if(r.output.equals(output)) {
				addBackup(r)
				return@removeIf true
			}
			return@removeIf false
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL, priority = 2000, example = [Example(commented = true)])
	fun removeAll() {
		AtomizerRegister.INSTANCE.recipes.forEach { this::addBackup }
		AtomizerRegister.INSTANCE.recipes.clear()
	}

	@MethodDescription(type = MethodDescription.Type.QUERY)
	fun streamRecipes(): SimpleObjectStream<AtomizerRecipe> {
		return SimpleObjectStream(AtomizerRegister.INSTANCE.recipes).setRemover { r -> remove(r) }
	}

	@RecipeBuilderDescription(
		example = [
			Example(".fluidInput(fluid('water') * 125).output(item('minecraft:clay'))"),
			Example(".fluidInput(fluid('lava') * 500).output(item('minecraft:gold_ingot')).reversible()")
		]
	)
	fun recipeBuilder(): RecipeBuilder {
		return RecipeBuilder()
	}

	@Property(property = "fluidInput", comp = Comp(eq = 1))
	@Property(property = "output", comp = Comp(eq = 1))
	class RecipeBuilder : AbstractRecipeBuilder<AtomizerRecipe>() {
		@Property
		private var reversible: Boolean = false;

		@RecipeBuilderMethodDescription(field = ["reversible"])
		fun reversible(reversible: Boolean): RecipeBuilder {
			this.reversible = reversible
			return this
		}

		@RecipeBuilderMethodDescription(field = ["reversible"])
		fun reversible(): RecipeBuilder {
			this.reversible = !this.reversible
			return this
		}

		override fun getErrorMsg(): String? {
			return "An error occurred while building an Alchemistry Atomizer recipe."
		}

		override fun validate(msg: GroovyLog.Msg?) {
			validateItems(msg, 0, 0, 1, 1)
			validateFluids(msg, 1, 1, 0, 0)
		}

		@Nullable
		@RecipeBuilderRegistrationMethod
		override fun register(): AtomizerRecipe? {
			if(!validate()) return null
			val recipe = AtomizerRecipe(false, fluidInput[0], output[0])
			if(reversible) {
				val reverse = LiquifierRecipe(output[0], fluidInput[0])
				GSPlugin.instance?.liquifier?.add(reverse)
			}
			GSPlugin.instance?.atomizer?.add(recipe)
			return recipe
		}
	}
}
