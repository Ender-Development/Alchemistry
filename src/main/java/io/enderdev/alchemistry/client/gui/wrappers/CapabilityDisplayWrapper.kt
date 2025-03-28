package io.enderdev.alchemistry.client.gui.wrappers

abstract class CapabilityDisplayWrapper(val x: Int, val y: Int, val width: Int, val height: Int) {
	abstract fun getStored(): Int
	abstract fun getCapacity(): Int
	abstract fun toStringList(): List<String>
}