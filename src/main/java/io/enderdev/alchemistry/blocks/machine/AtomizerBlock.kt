package io.enderdev.alchemistry.blocks.machine

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.items.TooltipItemBlock
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.event.RegistryEvent
import org.ender_development.catalyx.utils.extensions.translate

class AtomizerBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int) : ModelMachineBlock(name, tileClass, guiID, AxisAlignedBB(.0, .0, .0, 1.0, 1.0, 1.0)) {
	override fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(
			TooltipItemBlock(
				this,
				"tooltip.alchemistry.energy_requirement".translate(ConfigHandler.ATOMIZER.energyPerTick)
			)
				//.translate() + " " + ConfigHandler.atomizerEnergyPerTick + " FE/t")
				.setRegistryName(this.registryName)
		)
	}
}
