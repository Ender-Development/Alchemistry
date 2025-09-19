package io.enderdev.alchemistry.blocks

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.blocks.machine.IHasModel
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.ender_development.catalyx.blocks.BaseBlock

open class ModelBlock(name: String, material: Material = Material.ROCK) : BaseBlock(Alchemistry.catalyxSettings, name, material), IHasModel {
	@SideOnly(Side.CLIENT)
	override fun registerModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}
