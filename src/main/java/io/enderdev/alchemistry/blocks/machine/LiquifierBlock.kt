package io.enderdev.alchemistry.blocks.machine

import io.enderdev.alchemistry.ConfigHandler
import net.minecraft.block.state.IBlockState
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import org.ender_development.catalyx.items.TooltipItemBlock
import org.ender_development.catalyx.utils.extensions.translate
import kotlin.math.roundToInt

class LiquifierBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int) : BaseMachineBlock(name, tileClass, guiID, AxisAlignedBB(.0, .0, .0, 1.0, 1.0, 1.0)) {
	override val item = TooltipItemBlock(this, "tooltip.alchemistry.energy_requirement".translate(ConfigHandler.LIQUIFIER.energyPerTick))

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
