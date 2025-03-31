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
import io.enderdev.alchemistry.recipes.CombinerRecipe
import io.enderdev.alchemistry.recipes.DissolverRecipe
import io.enderdev.alchemistry.recipes.ProbabilityGroup
import io.enderdev.alchemistry.recipes.ProbabilitySet
import io.enderdev.alchemistry.recipes.register.DissolverRegister
import net.minecraft.item.ItemStack
import org.jetbrains.annotations.Nullable

@RegistryDescription(linkGenerator = Tags.MOD_ID)
class Dissolver : VirtualizedRegistry<DissolverRecipe>() {
	@GroovyBlacklist
	override fun onReload() {
		DissolverRegister.INSTANCE.recipes.removeAll(removeScripted())
		DissolverRegister.INSTANCE.recipes.addAll(restoreFromBackup())
	}

	@MethodDescription(type = MethodDescription.Type.ADDITION)
	fun add(recipe: DissolverRecipe?) {
		recipe?.let {
			addScripted(recipe)
			DissolverRegister.INSTANCE.recipes.add(recipe)
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL)
	fun remove(recipe: DissolverRecipe?): Boolean {
		if(DissolverRegister.INSTANCE.recipes.removeIf { r -> r == recipe }) {
			addBackup(recipe)
			return true
		}
		return false
	}

	@MethodDescription(
		type = MethodDescription.Type.REMOVAL,
		example = [Example(value = "item('alchemistry:compound:1')", commented = true)]
	)
	fun removeByInput(input: IIngredient): Boolean {
		return DissolverRegister.INSTANCE.recipes.removeIf { r ->
			if(r.input!!.matchingStacks.any { input.equals(it) }) {
				addBackup(r)
				return@removeIf true
			}
			return@removeIf false
		}
	}

	@MethodDescription(type = MethodDescription.Type.REMOVAL, priority = 2000, example = [Example(commented = true)])
	fun removeAll() {
		DissolverRegister.INSTANCE.recipes.forEach { this::addBackup }
		DissolverRegister.INSTANCE.recipes.clear()
	}

	@MethodDescription(type = MethodDescription.Type.QUERY)
	fun streamRecipes(): SimpleObjectStream<DissolverRecipe> {
		return SimpleObjectStream(DissolverRegister.INSTANCE.recipes).setRemover { r -> remove(r) }
	}

	@RecipeBuilderDescription(
		example = [
			Example(".input(item('minecraft:gold_ingot')).probabilityOutput(item('minecraft:clay')).reversible().rolls(1)"),
			Example(".input(item('minecraft:diamond')).probabilityOutput(30, item('minecraft:clay')).probabilityOutput(30, item('minecraft:clay')).probabilityOutput(30, item('minecraft:clay')).rolls(10)")
		]
	)
	fun recipeBuilder(): RecipeBuilder {
		return RecipeBuilder()
	}

	@Property(property = "input", comp = Comp(eq = 1))
	@Property(property = "output", comp = Comp(eq = 1))
	class RecipeBuilder : AbstractRecipeBuilder<DissolverRecipe>() {

		@Property(comp = Comp(gte = 1))
		val probabilityGroup: MutableList<ProbabilityGroup> = ArrayList();

		@Property
		var reversible: Boolean = false

		@Property
		var relativeProbability: Boolean = true

		@Property(defaultValue = "1", comp = Comp(gte = 1))
		var rolls: Int = 1;

		@RecipeBuilderMethodDescription(field = ["probabilityGroup"])
		fun probabilityOutput(probability: Double, vararg probabilityOutputs: ItemStack): RecipeBuilder {
			probabilityGroup.add(ProbabilityGroup(probabilityOutputs.toList(), probability));
			return this;
		}

		@RecipeBuilderMethodDescription(field = ["probabilityGroup"])
		fun probabilityOutput(vararg probabilityOutputs: ItemStack): RecipeBuilder {
			return this.probabilityOutput(100.0, probabilityOutputs.toList());
		}

		@RecipeBuilderMethodDescription(field = ["probabilityGroup"])
		fun probabilityOutput(probability: Double, probabilityOutputs: Collection<ItemStack>): RecipeBuilder {
			probabilityGroup.add(ProbabilityGroup(probabilityOutputs as List<ItemStack>, probability));
			return this;
		}

		@RecipeBuilderMethodDescription(field = ["probabilityGroup"])
		fun probabilityOutput(probabilityOutputs: Collection<ItemStack>): RecipeBuilder {
			return this.probabilityOutput(100.0, probabilityOutputs);
		}

		@RecipeBuilderMethodDescription(field = ["probabilityGroup"])
		override fun output(vararg probabilityOutputs: ItemStack): RecipeBuilder {
			return this.probabilityOutput(100.0, probabilityOutputs.toList());
		}

		@RecipeBuilderMethodDescription(field = ["probabilityGroup"])
		override fun output(probabilityOutputs: Collection<ItemStack>): RecipeBuilder {
			return this.probabilityOutput(100.0, probabilityOutputs);
		}

		@RecipeBuilderMethodDescription
		fun reversible(reversible: Boolean): RecipeBuilder {
			this.reversible = reversible;
			return this;
		}

		@RecipeBuilderMethodDescription
		fun reversible(): RecipeBuilder {
			this.reversible = !reversible;
			return this;
		}

		@RecipeBuilderMethodDescription
		fun relativeProbability(relativeProbability: Boolean): RecipeBuilder {
			this.relativeProbability = relativeProbability;
			return this;
		}

		@RecipeBuilderMethodDescription
		fun relativeProbability(): RecipeBuilder {
			this.relativeProbability = !relativeProbability;
			return this;
		}

		@RecipeBuilderMethodDescription
		fun rolls(rolls: Int): RecipeBuilder {
			this.rolls = rolls;
			return this;
		}

		override fun getErrorMsg(): String? {
			return "An error occurred while building an Alchemistry Dissolver recipe."
		}

		override fun validate(msg: GroovyLog.Msg) {
			validateItems(msg, 1, 1, 0, 0);
			validateFluids(msg);
			validateCustom(msg, probabilityGroup, 1, Integer.MAX_VALUE, "probability group");
			msg.add(rolls < 1, "rolls must be greater than or equal to 1, yet it was {}", rolls);
		}

		@Nullable
		@RecipeBuilderRegistrationMethod
		override fun register(): DissolverRecipe? {
			if(!validate()) return null

			val recipe = DissolverRecipe(
				input[0].toMcIngredient(),
				false,
				ProbabilitySet(probabilityGroup, relativeProbability, rolls)
			)
			if(reversible) {
				GSPlugin.instance?.combiner?.add(
					CombinerRecipe(
						recipe.inputs[0],
						probabilityGroup.map(ProbabilityGroup::output).flatten().toList(),
						""
					)
				)
			}
			GSPlugin.instance?.dissolver?.add(recipe)
			return recipe
		}
	}
}
