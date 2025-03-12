package io.enderdev.alchemistry.compat.ct

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.recipes.DissolverRecipe
import io.enderdev.alchemistry.recipes.ProbabilityGroup
import io.enderdev.alchemistry.recipes.ProbabilitySet
import io.enderdev.alchemistry.recipes.register.DissolverRegister
import io.enderdev.alchemistry.utils.extensions.toOre
import al132.alib.utils.extensions.containsItem
import al132.alib.utils.extensions.equalsIgnoreMeta
import crafttweaker.IAction
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IIngredient
import crafttweaker.api.item.IItemStack
import crafttweaker.api.oredict.IOreDictEntry
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraftforge.oredict.OreDictionary
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.alchemistry.Dissolver")
@ModOnly("alchemistry")
@ZenRegister
object CTChemicalDissolver {

    @ZenMethod
    @JvmStatic
    fun addRecipe(input: IIngredient, relativeProbability: Boolean, rolls: Int, outputs: Array<Array<Any?>>) {
        Alchemistry.LATE_ADDITIONS.add(object : IAction {
            override fun describe(): String? = "Added Chemical Dissolver Recipe for [$input]"

            override fun apply() {
                val groups = ArrayList<ProbabilityGroup>()
                outputs.forEach { rawArray ->
                    val probability = if (rawArray[0] is Double) rawArray[0] as Double else (rawArray[0] as Int).toDouble()
                    val group = rawArray.drop(1).map {
                        if (it == null) ItemStack.EMPTY
                        else (it as IItemStack).internal as ItemStack
                    }
                    groups.add(ProbabilityGroup(group, probability))
                }
                val outputSet = ProbabilitySet(_set = groups, relativeProbability = relativeProbability, rolls = rolls)
                if (input is IOreDictEntry) {
                    DissolverRegister.Companion.INSTANCE.recipes.add(
                        DissolverRecipe(
                            input.name.toOre(),
                            false,
                            outputSet
                        )
                    )
                } else if (input is IItemStack) {
                    DissolverRegister.Companion.INSTANCE.recipes.add(
                        DissolverRecipe(
                            Ingredient.fromStacks(input.internal as ItemStack),
                            false,
                            outputSet
                        )
                    )
                }
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IIngredient) {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe(): String? = "Removed Chemical Dissolver Recipe for [$input]"

            override fun apply() {
                val inputStack = input.internal
                if (inputStack is ItemStack) DissolverRegister.Companion.INSTANCE.recipes.removeIf { it.inputs.containsItem(inputStack) }
                else if (inputStack is String) {
                    DissolverRegister.Companion.INSTANCE.recipes.removeIf { recipe ->
                        if (recipe.inputs.isNotEmpty() && OreDictionary.getOres(inputStack).isNotEmpty()) {
                            val inputEntry = OreDictionary.getOres(inputStack)[0]
                            val recipeEntry = recipe.inputs[0]
                            recipeEntry.equalsIgnoreMeta(inputEntry)
                        } else false
                    }
                }
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeAllRecipes() {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Removed ALL Chemical Dissolver recipes"

            override fun apply() = DissolverRegister.Companion.INSTANCE.recipes.clear()
        })
    }
}