package io.enderdev.alchemistry.items

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.blocks.machine.IHasModel
import io.enderdev.catalyx.items.BaseItem
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBase(val name: String) : BaseItem(Alchemistry.catalyxSettings, name), IHasModel {
	@SideOnly(Side.CLIENT)
	override fun registerModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
	}
}
