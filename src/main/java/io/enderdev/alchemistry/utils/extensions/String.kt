package io.enderdev.alchemistry.utils.extensions

import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import net.minecraft.item.ItemStack
import org.ender_development.catalyx.utils.extensions.toStack

fun String.chemical(quantity: Int = 1, meta: Int = 0): ItemStack {
	ElementRegistry[this]?.apply { return toItemStack(quantity) }
	CompoundRegistry[this]?.apply { return toItemStack(quantity) }

	return toStack(quantity, meta)
}
