package io.enderdev.alchemistry.compat.groovyscript.register

import com.cleanroommc.groovyscript.api.GroovyBlacklist
import com.cleanroommc.groovyscript.api.GroovyLog
import com.cleanroommc.groovyscript.api.IIngredient
import com.cleanroommc.groovyscript.api.documentation.annotations.*
import com.cleanroommc.groovyscript.helper.SimpleObjectStream
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.compat.groovyscript.GSPlugin
import io.enderdev.alchemistry.recipes.CombinerRecipe
import io.enderdev.alchemistry.recipes.register.CombinerRegister
import net.minecraft.item.ItemStack
import org.jetbrains.annotations.Nullable
import java.util.stream.Collectors

@RegistryDescription(linkGenerator = Tags.MOD_ID)
class Combiner : VirtualizedRegistry<CombinerRecipe>() {
	@GroovyBlacklist
	override fun onReload() {
		CombinerRegister.INSTANCE.recipes.removeAll(removeScripted())
		CombinerRegister.INSTANCE.recipes.addAll(restoreFromBackup())
	}

	@MethodDescription(type = MethodDescription.Type.ADDITION)
	fun add(recipe: CombinerRecipe?) {
		recipe?.let {
			addScripted(recipe)
			CombinerRegister.INSTANCE.recipes.add(recipe)
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL)
	fun remove(recipe: CombinerRecipe?): Boolean {
		if(CombinerRegister.INSTANCE.recipes.removeIf { r -> r == recipe }) {
			addBackup(recipe)
			return true
		}
		return false
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL, example = [Example("item('minecraft:glowstone')")])
	fun removeByOutput(output: IIngredient): Boolean {
		return CombinerRegister.INSTANCE.recipes.removeIf { r ->
			if(output.test(r.output)) {
				addBackup(r);
				return@removeIf true;
			}
			return@removeIf false;
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL, example = [Example("element('carbon')")])
	fun removeByInput(input: IIngredient): Boolean {
		return CombinerRegister.INSTANCE.recipes.removeIf { r ->
			if(r.inputs.any { input.test(it) }) {
				addBackup(r);
				return@removeIf true;
			}
			return@removeIf false;
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL, priority = 2000, example = [Example(commented = true)])
	fun removeAll() {
		CombinerRegister.INSTANCE.recipes.forEach { this::addBackup }
		CombinerRegister.INSTANCE.recipes.clear()
	}

	@MethodDescription(type = MethodDescription.Type.QUERY)
	fun streamRecipes(): SimpleObjectStream<CombinerRecipe> {
		return SimpleObjectStream(CombinerRegister.INSTANCE.recipes).setRemover { r -> remove(r) }
	}

	@RecipeBuilderDescription(
		example = [
			Example(".input(item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2, item('minecraft:gold_ingot') * 2).output(item('minecraft:gold_block') * 2)"),
			Example(".input(ItemStack.EMPTY, ItemStack.EMPTY, item('minecraft:clay')).output(item('minecraft:gold_ingot'))")
		]
	)
	fun recipeBuilder(): RecipeBuilder {
		return RecipeBuilder()
	}

	@Property(property = "input", comp = Comp(gte = 1, lte = 9))
	@Property(property = "output", comp = Comp(eq = 1))
	class RecipeBuilder : AbstractRecipeBuilder<CombinerRecipe>() {
		@Property
		var gamestage: String = ""

		@RecipeBuilderMethodDescription
		fun gamestage(gamestage: String): RecipeBuilder {
			this.gamestage = gamestage
			return this
		}

		override fun getErrorMsg(): String? {
			return "Error adding Alchemistry Combiner recipe";
		}

		override fun validate(msg: GroovyLog.Msg) {
			val inputSize: Int = input.getRealSize();
			output.trim();
			msg.add(inputSize < 1 || inputSize > 9, "Must have 1 - 9 inputs, but found {}", input.size);
			msg.add(output.size != 1, "Must have exactly 1 output, but found {}", output.size);
			validateFluids(msg);
		}

		@Nullable
		@RecipeBuilderRegistrationMethod
		override fun register(): CombinerRecipe? {
			if(!validate()) return null;

			val inputs: List<ItemStack> =
				input.stream().map { x -> if(x.isEmpty) ItemStack.EMPTY else IngredientHelper.toItemStack(x) }
					.collect(Collectors.toList())
			val recipe = CombinerRecipe(output[0], inputs, gamestage);
			GSPlugin.instance?.combiner?.add(recipe)
			return recipe;
		}
	}
}
