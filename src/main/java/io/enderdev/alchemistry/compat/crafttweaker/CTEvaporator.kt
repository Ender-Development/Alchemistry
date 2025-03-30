package io.enderdev.alchemistry.compat.crafttweaker


import crafttweaker.IAction
import crafttweaker.annotations.ModOnly
import crafttweaker.annotations.ZenRegister
import crafttweaker.api.item.IItemStack
import crafttweaker.api.liquid.ILiquidStack
import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.Reference
import io.enderdev.alchemistry.recipes.EvaporatorRecipe
import io.enderdev.alchemistry.recipes.register.EvaporatorRegister
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import stanhebben.zenscript.annotations.ZenClass
import stanhebben.zenscript.annotations.ZenMethod

@ZenClass("mods.${Reference.MODID}.Evaporator")
@ModOnly(Reference.MODID)
@ZenRegister
object CTEvaporator {

    @ZenMethod
    @JvmStatic
    fun addRecipe(output: IItemStack, input: ILiquidStack) {
        Alchemistry.LATE_ADDITIONS.add(object : IAction {
            override fun describe() = "Added Evaporator recipe for [$input] -> [$output]"

            override fun apply() {
                val inputStack = input.internal as FluidStack
                val outputStack = output.internal as ItemStack
                EvaporatorRegister.Companion.INSTANCE.recipes.add(EvaporatorRecipe(inputStack, outputStack))
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeRecipe(input: ILiquidStack) {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Removed Evaporator recipe for [$input]"

            override fun apply() {
                val inputStack = input.internal as FluidStack
                EvaporatorRegister.Companion.INSTANCE.recipes.removeIf { it.input.isFluidEqual(inputStack) }
            }
        })
    }

    @ZenMethod
    @JvmStatic
    fun removeAllRecipes() {
        Alchemistry.LATE_REMOVALS.add(object : IAction {
            override fun describe() = "Removed ALL Evaporator recipes"

            override fun apply() = EvaporatorRegister.Companion.INSTANCE.recipes.clear()
        })
    }
}