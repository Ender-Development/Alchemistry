package io.enderdev.alchemistry.blocks.machine

import io.enderdev.alchemistry.client.TESREvaporator
import io.enderdev.alchemistry.items.TooltipItemBlock
import io.enderdev.alchemistry.tiles.TileEvaporator
import io.enderdev.catalyx.utils.extensions.translate
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry


class EvaporatorBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int) : ModelMachineBlock(
	name,
	tileClass,
	guiID,
	AxisAlignedBB(.0625, .0625, .0625, .9375, .75, .9375),
	AxisAlignedBB(.25, .0, .25, .75, .0625, .75)
) {
	override fun registerModel() {
		super.registerModel()
		ClientRegistry.bindTileEntitySpecialRenderer(TileEvaporator::class.java, TESREvaporator())
	}

	override fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(
			TooltipItemBlock(this, "tile.evaporator.tooltip".translate())
				.setRegistryName(this.registryName)
		)
	}
}
