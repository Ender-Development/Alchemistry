package io.enderdev.alchemistry.utils.extensions

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

fun Fluid.toStack(quantity: Int) = FluidStack(this, quantity)