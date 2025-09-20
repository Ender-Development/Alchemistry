package io.enderdev.alchemistry.blocks

import io.enderdev.alchemistry.Alchemistry
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.EnumHand
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseBlock

class CoreBlock(name: String) : BaseBlock(Alchemistry.catalyxSettings, name) {
	companion object {
		val AXIS = PropertyEnum.create("axis", Axis::class.java)
		val PROPERTIES = arrayOf<IProperty<*>>(AXIS)
	}

	init {
		defaultState = blockState.baseState.withProperty(AXIS, Axis.Y)
	}

	// stole these from BlockRotatedPillar

	override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
		val state = world.getBlockState(pos)
		world.setBlockState(pos, state.cycleProperty(AXIS))
		return true
	}

	@Deprecated("")
	override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
		if(rot != Rotation.COUNTERCLOCKWISE_90 && rot != Rotation.CLOCKWISE_90)
			return state

		val rot = state.getValue(AXIS)
		if(rot == Axis.X)
			return state.withProperty(AXIS, Axis.Z)
		if(rot == Axis.Z)
			return state.withProperty(AXIS, Axis.X)

		return state
	}

	@Deprecated("")
	override fun getStateFromMeta(meta: Int): IBlockState {
		val r = meta and 12 // 12 = 4 | 8
		return defaultState.withProperty(AXIS, if(r == 4) Axis.X else if(r == 8) Axis.Z else Axis.Y)
	}

	override fun getMetaFromState(state: IBlockState): Int {
		val axis = state.getValue(AXIS)
		return if(axis == Axis.X) 4 else if(axis == Axis.Z) 8 else 0
	}

	override fun getLightValue(state: IBlockState, world: IBlockAccess, pos: BlockPos): Int = 4

	override fun createBlockState() = BlockStateContainer(this, *PROPERTIES)

	override fun getStateForPlacement(
		world: World,
		pos: BlockPos,
		facing: EnumFacing,
		hitX: Float,
		hitY: Float,
		hitZ: Float,
		meta: Int,
		placer: EntityLivingBase,
		hand: EnumHand
	): IBlockState {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(AXIS, facing.axis)
	}
}
