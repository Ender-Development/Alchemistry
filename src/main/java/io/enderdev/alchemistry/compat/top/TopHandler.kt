package io.enderdev.alchemistry.compat.top

import mcjty.theoneprobe.TheOneProbe
import mcjty.theoneprobe.apiimpl.TheOneProbeImp

object TopHandler {
	fun register() {
		val theOneProbeImp: TheOneProbeImp = TheOneProbe.theOneProbeImp;
		theOneProbeImp.registerProvider(TopReactor());
	}
}
