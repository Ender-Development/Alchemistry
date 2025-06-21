package io.enderdev.alchemistry.blocks.machine

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.items.TooltipItemBlock
import io.enderdev.catalyx.utils.extensions.translate
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import kotlin.math.roundToInt

class LiquifierBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int) : ModelMachineBlock(name, tileClass, guiID, AxisAlignedBB(.0, .0, .0, 1.0, 1.0, 1.0)) {

	override fun registerItem(event: RegistryEvent.Register<Item>) {
		event.registry.register(
			TooltipItemBlock(
				this,
				"tooltip.alchemistry.energy_requirement".translate(ConfigHandler.LIQUIFIER.energyPerTick)
			)
				.setRegistryName(this.registryName)
		)
	}

	@Deprecated("")
	override fun getComparatorInputOverride(state: IBlockState, world: World, pos: BlockPos): Int {
		val te = world.getTileEntity(pos)
		if(te == null)
			return 0

		val cap = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
		if(cap == null)
			return 0

		val properties = cap.tankProperties[0]
		if(properties.contents == null || properties.contents!!.amount == 0)
			return 0

		return (properties.contents!!.amount.toFloat() / properties.capacity * 15).roundToInt()
	}
}
