package io.enderdev.alchemistry.items

abstract class ItemMetaBase(name: String) : ItemBase(name) {
	init {
		hasSubtypes = true
	}
}
