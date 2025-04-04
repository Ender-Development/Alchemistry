package io.enderdev.alchemistry.blocks

import net.minecraft.util.IStringSerializable

enum class PropertyPowerStatus(val statusName: String) : IStringSerializable {
	OFF("off"),
	STANDBY("standby"),
	ON("on");

	override fun getName() = statusName
}
