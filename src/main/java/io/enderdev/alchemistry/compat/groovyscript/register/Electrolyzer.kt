package io.enderdev.alchemistry.compat.groovyscript.register

import com.cleanroommc.groovyscript.api.GroovyBlacklist
import com.cleanroommc.groovyscript.api.GroovyLog
import com.cleanroommc.groovyscript.api.IIngredient
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp
import com.cleanroommc.groovyscript.api.documentation.annotations.Example
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription
import com.cleanroommc.groovyscript.api.documentation.annotations.Property
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderMethodDescription
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription
import com.cleanroommc.groovyscript.helper.SimpleObjectStream
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.compat.groovyscript.GSPlugin
import io.enderdev.alchemistry.recipes.ElectrolyzerRecipe
import io.enderdev.alchemistry.recipes.register.ElectrolyzerRegister
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.fluids.FluidStack

@RegistryDescription(linkGenerator = Tags.MOD_ID)
class Electrolyzer : VirtualizedRegistry<ElectrolyzerRecipe>() {
    @GroovyBlacklist
    override fun onReload() {
        ElectrolyzerRegister.INSTANCE.recipes.removeAll(removeScripted())
        ElectrolyzerRegister.INSTANCE.recipes.addAll(restoreFromBackup())
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    fun add(recipe: ElectrolyzerRecipe?) {
        recipe?.let {
            addScripted(recipe)
            ElectrolyzerRegister.INSTANCE.recipes.add(recipe)
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    fun remove(recipe: ElectrolyzerRecipe?): Boolean {
        if (ElectrolyzerRegister.INSTANCE.recipes.removeIf { r -> r == recipe }) {
            addBackup(recipe)
            return true
        }
        return false
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = [Example("element('chlorine')")])
    fun removeByOutput(output: IIngredient): Boolean {
        return ElectrolyzerRegister.INSTANCE.recipes.removeIf { r ->
            if (r.outputs.any { o -> output.test(o) }) {
                addBackup(r)
                return@removeIf true
            }
            return@removeIf false
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = [Example("element('calcium_carbonate')")])
    fun removeByInput(input: IIngredient): Boolean {
        return ElectrolyzerRegister.INSTANCE.recipes.removeIf { r ->
            if (r.electrolytes.any { e -> input.test(e) }) {
                addBackup(r)
                return@removeIf true
            }
            return@removeIf false
        }
    }

    @MethodDescription(example = [Example(value = "fluid('water')", commented = true)])
    fun removeByInput(input: FluidStack): Boolean {
        return ElectrolyzerRegister.INSTANCE.recipes.removeIf { r ->
            if (r.input.isFluidEqual(input)) {
                addBackup(r)
                return@removeIf true
            }
            return@removeIf false
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, priority = 2000, example = [Example(commented = true)])
    fun removeAll() {
        ElectrolyzerRegister.INSTANCE.recipes.forEach { this::addBackup }
        ElectrolyzerRegister.INSTANCE.recipes.clear()
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    fun streamRecipes(): SimpleObjectStream<ElectrolyzerRecipe> {
        return SimpleObjectStream(ElectrolyzerRegister.INSTANCE.recipes).setRemover { r -> remove(r) }
    }

    @RecipeBuilderDescription(
        example = [
            Example(".fluidInput(fluid('lava') * 100).output(item('minecraft:clay'))"),
            Example(".fluidInput(fluid('water') * 100).input(item('minecraft:gold_ingot')).consumptionChance(100).output(item('minecraft:gold_nugget') * 4).output(item('minecraft:gold_nugget') * 4).output(item('minecraft:gold_nugget') * 4).output(item('minecraft:gold_nugget') * 4).chance(50).chance(50)")
        ]
    )
    fun recipeBuilder(): RecipeBuilder {
        return RecipeBuilder()
    }

    @Property(property = "input", comp = Comp(gte = 0, lte = 1))
    @Property(property = "output", comp = Comp(gte = 1, lte = 4))
    class RecipeBuilder : AbstractRecipeBuilder<ElectrolyzerRecipe>() {
        @Property(comp = Comp(gte = 0, lte = 2))
        val chance: ArrayList<Int> = ArrayList(2)

        @Property(comp = Comp(gte = 0, lte = 100))
        var consumptionChance: Int = 0;

        @RecipeBuilderMethodDescription
        fun input(ingredient: IIngredient, chance: Int): RecipeBuilder {
            this.input.add(ingredient);
            this.chance.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription
        fun chance(chance: Int): RecipeBuilder {
            this.chance.add(chance);
            return this;
        }

        @RecipeBuilderMethodDescription
        fun chance(vararg chances: Int): RecipeBuilder {
            chances.forEach { chance(it) }
            return this
        }

        @RecipeBuilderMethodDescription
        fun chance(chances: Collection<Int>): RecipeBuilder {
            chances.forEach { chance(it) }
            return this;
        }

        @RecipeBuilderMethodDescription
        fun consumptionChance(consumptionChance: Int): RecipeBuilder {
            this.consumptionChance = consumptionChance;
            return this;
        }

        override fun getErrorMsg(): String? {
            return "An error occurred while building an Alchemistry Electrolyzer recipe."
        }

        override fun validate(msg: GroovyLog.Msg?) {
            validateItems(msg, 0, 1, 1, 4);
            validateFluids(msg, 1, 1, 0, 0);
            validateCustom(msg, chance, 0, 2, "chance");
            msg?.add(
                !chance.isEmpty() && chance.size > (output.size - 2),
                "chance only applies to output items after the second, cannot have more chance than output items above 2, had {} chance and {} output",
                chance.size,
                output.size
            );
            msg?.add(
                consumptionChance < 0 || consumptionChance > 100,
                "consumption chance must be between 0 and 100, yet it was {}",
                consumptionChance
            );
        }

        override fun register(): ElectrolyzerRecipe? {
            if (!validate()) return null;
            val recipe = ElectrolyzerRecipe(
                fluidInput.get(0),
                if (input.isNotEmpty()) input[0].toMcIngredient() else Ingredient.EMPTY,
                consumptionChance,
                output[0],
                output.getOrEmpty(1),
                output.getOrEmpty(2),
                if (chance.isNotEmpty()) chance[0] else 0,
                output.getOrEmpty(3),
                if (chance.size >= 2) chance[1] else 0
            )
            GSPlugin.instance?.electrolyzer?.add(recipe)
            return recipe;
        }
    }
}