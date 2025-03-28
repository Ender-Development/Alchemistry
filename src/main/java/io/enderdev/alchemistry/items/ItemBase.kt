package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.Reference
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBase(val name: String) : Item() {
	init {
		ModItems.items.add(this)
		registryName = ResourceLocation(Reference.MODID, name)
		translationKey = "$registryName"
		creativeTab = Reference.creativeTab
	}

	open fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(this)
	}

	@SideOnly(Side.CLIENT)
	open fun registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}