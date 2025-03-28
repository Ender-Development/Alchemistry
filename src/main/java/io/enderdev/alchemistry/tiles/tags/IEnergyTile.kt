package io.enderdev.alchemistry.tiles.tags

import net.minecraftforge.energy.IEnergyStorage

interface IEnergyTile {
	var energyStorage: IEnergyStorage
	fun energyCapacity(): Int
}