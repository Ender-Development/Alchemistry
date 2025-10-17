package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.Alchemistry
import org.ender_development.catalyx.items.BaseItem

abstract class ItemMetaBase(name: String) : BaseItem(Alchemistry, name) {
	init {
		hasSubtypes = true
	}
}
