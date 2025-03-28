package io.enderdev.alchemistry.tiles.tags

import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

interface IFluidTile {
	val fluidTanks: FluidHandlerConcatenate?
		get() = null
}