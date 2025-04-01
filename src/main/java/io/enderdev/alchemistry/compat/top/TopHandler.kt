package io.enderdev.alchemistry.compat.top

import mcjty.theoneprobe.TheOneProbe

object TopHandler {
	fun register() {
		val top = TheOneProbe.theOneProbeImp
		top.registerProvider(TopReactor())
		top.registerProvider(TopChemicalCombiner())
		top.registerProvider(TopChemicalDissolver())
		top.registerProvider(TopEvaporator())
		top.registerProvider(TopAtomizer())
		top.registerProvider(TopLiquifier())
	}
}
