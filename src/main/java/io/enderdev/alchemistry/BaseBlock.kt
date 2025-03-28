package io.enderdev.alchemistry

import io.enderdev.alchemistry.blocks.ModBlocks
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class BaseBlock(name: String, material: Material = Material.ROCK) : Block(material) {
	init {
		ModBlocks.blocks.add(this)
		translationKey = name
		registryName = ResourceLocation(Reference.MODID, name)
		blockHardness = 3f
		creativeTab = Reference.creativeTab
	}

	open fun registerBlock(event: RegistryEvent.Register<Block>) {
		event.registry.register(this)
	}

	open fun registerItemBlock(event: RegistryEvent.Register<Item>) {
		event.registry.register(ItemBlock(this).setRegistryName(registryName))
	}

	@SideOnly(Side.CLIENT)
	open fun registerModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}