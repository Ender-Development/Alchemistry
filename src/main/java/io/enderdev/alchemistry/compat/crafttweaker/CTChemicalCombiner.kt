package io.enderdev.alchemistry.compat.crafttweaker

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.recipes.CombinerRecipe
import io.enderdev.alchemistry.recipes.register.CombinerRegister
import al132.alib.utils.extensions.areItemStacksEqual
import crafttweaker.IAction
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import net.minecraft.item.ItemStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.alchemistry.Combiner")
@ModOnly("alchemistry")
@ZenRegister
object CTChemicalCombiner {

    @ZenMethod
    @JvmStatic
    fun addRecipe(output: IItemStack?, input: Array<IItemStack?>) {
        Alchemistry.LATE_ADDITIONS.add(object : IAction {
            override fun describe(): String? = "Added Chemical Combiner recipe for [${input.toList()}]->[$output]"

            override fun apply() {
                val inputStacks = input.toList()
                val outputStack: ItemStack? = output?.internal as? ItemStack
                if (outputStack != null) {
                    val recipe = CombinerRecipe(outputStack, inputStacks.map { stack: IItemStack? ->
                        (stack?.internal as? ItemStack) ?: ItemStack.EMPTY
                    })
                    CombinerRegister.Companion.INSTANCE.recipes.add(recipe)
                } else Alchemistry.logger.info("Unable to add crafttweaker recipe")
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun addStagedRecipe(output: IItemStack?, input: Array<IItemStack?>, stage: String) {
        Alchemistry.LATE_ADDITIONS.add(object : IAction {
            override fun describe(): String? =
                    "Added Chemical Combiner recipe for [${input.toList()}]->[$output], requiring stage: $stage"

            override fun apply() {
                val inputStacks = input.toList()
                val outputStack: ItemStack? = output?.internal as? ItemStack
                if (outputStack != null) {
                    val recipe = CombinerRecipe(outputStack, inputStacks.map { stack: IItemStack? ->
                        (stack?.internal as? ItemStack) ?: ItemStack.EMPTY
                    }, stage)
                    CombinerRegister.Companion.INSTANCE.recipes.add(recipe)
                } else Alchemistry.logger.info("Unable to add crafttweaker recipe")
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun setAsStaged(output: IItemStack?, stage: String) {
        Alchemistry.LATE_ADDITIONS.add(object : IAction {
            override fun describe(): String? = "Chemical Combiner recipe is set to require stage [${stage}] for [$output]"

            override fun apply() {
                val outputStack: ItemStack? = output?.internal as? ItemStack
                if (outputStack != null) {
                    val matchingRecipe = CombinerRegister.Companion.INSTANCE.recipes.filter { it.output.areItemStacksEqual(outputStack) }
                    matchingRecipe.forEach { it.gamestage = stage }
                } else Alchemistry.logger.info("Unable to set crafttweaker recipe stage")
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: IItemStack?) {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe(): String? = "Removed Chemical Combiner recipe for [$input]"

            override fun apply() {
                val unwrappedInput = input?.internal as? ItemStack ?: ItemStack.EMPTY
                val recipe = CombinerRecipe.Companion.matchOutput(unwrappedInput)
                if (recipe != null) CombinerRegister.Companion.INSTANCE.recipes.remove(recipe)
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeAllRecipes() {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Removed ALL Chemical Combiner recipes"

            override fun apply() = CombinerRegister.Companion.INSTANCE.recipes.clear()
        })
    }
}