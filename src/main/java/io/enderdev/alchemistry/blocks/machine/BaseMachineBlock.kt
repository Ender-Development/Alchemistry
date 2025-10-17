package io.enderdev.alchemistry.blocks.machine

import io.enderdev.alchemistry.Alchemistry
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseMachineBlock

open class BaseMachineBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int, vararg val boundingBoxes: AxisAlignedBB) : BaseMachineBlock(Alchemistry, name, tileClass, guiID) {
	@Deprecated("")
	override fun getRenderType(state: IBlockState): EnumBlockRenderType = EnumBlockRenderType.MODEL

	@Deprecated("")
	override fun isOpaqueCube(state: IBlockState) = false

	@Deprecated("")
	override fun isFullCube(state: IBlockState) = false

	@Deprecated("")
	override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = boundingBoxes[0]

	@Deprecated("")
	override fun addCollisionBoxToList(
		state: IBlockState,
		worldIn: World,
		pos: BlockPos,
		entityBox: AxisAlignedBB,
		collidingBoxes: List<AxisAlignedBB>,
		entityIn: Entity?, mysteryboolean: Boolean
	) {
		boundingBoxes.forEach {
			@Suppress("DEPRECATION")
			addCollisionBoxToList(pos, entityBox, collidingBoxes, it)
		}
	}
}
