package io.enderdev.alchemistry.utils.extensions

import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.catalyx.utils.extensions.toStack
import net.minecraft.item.ItemStack

fun String.chemical(quantity: Int = 1, meta: Int = 0): ItemStack {
	ElementRegistry[this]?.apply { return toItemStack(quantity) }
	CompoundRegistry[this]?.apply { return toItemStack(quantity) }

	return toStack(quantity, meta)
}
