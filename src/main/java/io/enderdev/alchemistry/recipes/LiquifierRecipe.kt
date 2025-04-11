package io.enderdev.alchemistry.recipes

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

data class LiquifierRecipe(val input: ItemStack, val output: FluidStack) : IRecipe {
	constructor(input: ItemStack, fluid: Fluid, fluidQuantity: Int) : this(input, FluidStack(fluid, fluidQuantity))
}
