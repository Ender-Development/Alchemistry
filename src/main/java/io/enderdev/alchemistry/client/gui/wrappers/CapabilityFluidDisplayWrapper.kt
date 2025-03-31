package io.enderdev.alchemistry.client.gui.wrappers

import net.minecraftforge.fluids.IFluidTank

open class CapabilityFluidDisplayWrapper(x: Int, y: Int, width: Int, height: Int, val fluidTank: () -> IFluidTank) :
	CapabilityDisplayWrapper(x, y, width, height) {

	override fun getCapacity() = fluidTank().capacity
	override fun getStored() = fluidTank().fluidAmount
	override fun toStringList() = listOf("${getStored()}/${getCapacity()} mb ${fluidTank().fluid?.fluid?.getLocalizedName(fluidTank().fluid)?: ""}".trimEnd())

	fun getFluid() = fluidTank().fluid
}
