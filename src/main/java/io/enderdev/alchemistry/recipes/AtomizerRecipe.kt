package io.enderdev.alchemistry.recipes

import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack


data class AtomizerRecipe(val reversible: Boolean = false, val input: FluidStack, val output: ItemStack) : IRecipe {
	constructor(reversible: Boolean = false, fluid: Fluid, fluidQuantity: Int, output: ItemStack)
			: this(reversible, FluidStack(fluid, fluidQuantity), output)
}
