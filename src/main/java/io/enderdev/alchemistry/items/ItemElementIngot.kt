package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.chemistry.ElementRegistry
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.utils.extensions.translate

class ItemElementIngot(name: String) : ItemMetaBase(name) {
	@SideOnly(Side.CLIENT)
	override fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
		ElementRegistry.keys()
			.filter { it <= 118 && !invalidIngots.contains(it) }
			.forEach {
				ModelLoader.setCustomModelResourceLocation(
					this, it,
					ModelResourceLocation("$registryName", "inventory")
				)
			}
	}

	@SideOnly(Side.CLIENT)
	override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
		if(!isInCreativeTab(tab)) return
		ElementRegistry.keys()
			.filter { it <= 118 && !invalidIngots.contains(it) }
			.forEach { items.add(ItemStack(this, 1, it)) }
	}

	override fun getItemStackDisplayName(stack: ItemStack): String {
		var i = stack.metadata
		if(!ElementRegistry.keys().contains(i)) i = 1
		// val elementName = ModItems.elements.toStack(meta = i)
		return "item.${Tags.MOD_ID}:ingot_${ElementRegistry[i]!!.name}.name".translate()
	}

	companion object {
		val invalidIngots = listOf(1, 2, 6, 7, 8, 9, 10, 15, 16, 17, 18, 26, 35, 36, 53, 54, 79, 80, 86)
	}
}
