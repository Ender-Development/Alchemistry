package io.enderdev.alchemistry.compat.groovyscript.register

import com.cleanroommc.groovyscript.api.GroovyBlacklist
import com.cleanroommc.groovyscript.api.GroovyLog
import com.cleanroommc.groovyscript.api.IIngredient
import com.cleanroommc.groovyscript.api.documentation.annotations.Comp
import com.cleanroommc.groovyscript.api.documentation.annotations.Example
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription
import com.cleanroommc.groovyscript.api.documentation.annotations.Property
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription
import com.cleanroommc.groovyscript.helper.SimpleObjectStream
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.compat.groovyscript.GSPlugin
import io.enderdev.alchemistry.recipes.LiquifierRecipe
import io.enderdev.alchemistry.recipes.register.LiquifierRegister
import org.jetbrains.annotations.Nullable

@RegistryDescription(linkGenerator = Tags.MOD_ID)
class Liquifier : VirtualizedRegistry<LiquifierRecipe>() {

    @GroovyBlacklist
    override fun onReload() {
        LiquifierRegister.INSTANCE.recipes.removeAll(removeScripted())
        LiquifierRegister.INSTANCE.recipes.addAll(restoreFromBackup())
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    fun add(recipe: LiquifierRecipe?) {
        recipe?.let {
            addScripted(recipe)
            LiquifierRegister.INSTANCE.recipes.add(recipe)
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL)
    fun remove(recipe: LiquifierRecipe?): Boolean {
        if (LiquifierRegister.INSTANCE.recipes.removeIf { r -> r == recipe }) {
            addBackup(recipe)
            return true
        }
        return false
    }

    @MethodDescription(
        type = MethodDescription.Type.REMOVAL,
        example = [Example(value = "item('alchemistry:compound:7')", commented = true)]
    )
    fun removeByInput(input: IIngredient): Boolean {
        return LiquifierRegister.INSTANCE.recipes.removeIf { r ->
            if (input.matchingStacks.any { it.isItemEqual(r.input) }) {
                addBackup(r)
                return@removeIf true
            }
            return@removeIf false
        }
    }

    @MethodDescription(
        type = MethodDescription.Type.REMOVAL,
        example = [Example(value = "fluid('water')", commented = true)]
    )
    fun removeByOutput(output: IIngredient): Boolean {
        return LiquifierRegister.INSTANCE.recipes.removeIf { r ->
            if (r.output.equals(output)) {
                addBackup(r)
                return@removeIf true
            }
            return@removeIf false
        }
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, priority = 2000, example = [Example(commented = true)])
    fun removeAll() {
        LiquifierRegister.INSTANCE.recipes.forEach { this::addBackup }
        LiquifierRegister.INSTANCE.recipes.clear()
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    fun streamRecipes(): SimpleObjectStream<LiquifierRecipe> {
        return SimpleObjectStream(LiquifierRegister.INSTANCE.recipes).setRemover { r -> remove(r) }
    }

    @RecipeBuilderDescription(
        example = [
            Example(".fluidOutput(fluid('water') * 125).input(item('minecraft:dirt'))"),
            Example(".fluidOutput(fluid('lava') * 500).input(item('minecraft:diamond'))")
        ]
    )
    fun recipeBuilder(): RecipeBuilder {
        return RecipeBuilder()
    }

    @Property(property = "input", comp = Comp(eq = 1))
    @Property(property = "fluidOutput", comp = Comp(eq = 1))
    class RecipeBuilder : AbstractRecipeBuilder<LiquifierRecipe>() {
        override fun getErrorMsg(): String? {
            return "An error occurred while building an Alchemistry Liquifier recipe."
        }

        override fun validate(msg: GroovyLog.Msg?) {
            validateItems(msg, 1, 1, 0, 0)
            validateFluids(msg, 0, 0, 1, 1)
        }

        @Nullable
        @RecipeBuilderRegistrationMethod
        override fun register(): LiquifierRecipe? {
            if (!validate()) return null
            val recipe = LiquifierRecipe(IngredientHelper.toItemStack(input[0]), fluidOutput[0])
            GSPlugin.Companion.instance?.liquifier?.add(recipe)
            return recipe
        }
    }
}