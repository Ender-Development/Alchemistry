package io.enderdev.alchemistry.blocks.machine

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.items.TooltipItemBlock
import org.ender_development.catalyx.utils.extensions.translate

class ChemicalCombinerBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int) : BaseMachineBlock(name, tileClass, guiID, AxisAlignedBB(.0, .0, .0, 1.0, .875, 1.0)) {
	override val item = TooltipItemBlock(this, "tooltip.alchemistry.energy_requirement".translate(ConfigHandler.COMBINER.energyPerTick))

	override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack)
		val tile = world.getTileEntity(pos) as? TileChemicalCombiner
		tile?.owner = placer.name ?: ""
	}
}
