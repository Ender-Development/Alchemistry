package io.enderdev.alchemistry.client.gui.wrappers

import net.minecraftforge.energy.IEnergyStorage
import java.text.NumberFormat
import java.util.*

open class CapabilityEnergyDisplayWrapper(x: Int, y: Int, width: Int, height: Int, val energyStorage: () -> IEnergyStorage) :
	CapabilityDisplayWrapper(x, y, width, height) {

	override fun getStored() = energyStorage().energyStored
	override fun getCapacity() = energyStorage().maxEnergyStored

	private val numFormat = NumberFormat.getInstance(Locale.getDefault())

	override fun toStringList(): List<String> {
		val stored = numFormat.format(getStored())
		val capacity = numFormat.format(getCapacity())
		return listOf("$stored/$capacity FE")
	}
}