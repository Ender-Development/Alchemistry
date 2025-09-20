package io.enderdev.alchemistry.blocks.machine

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.blocks.PropertyPowerStatus
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.ender_development.catalyx.blocks.BaseMachineBlock
import org.ender_development.catalyx.items.TooltipItemBlock
import org.ender_development.catalyx.utils.extensions.translate

class ReactorControllerBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int, val energyPerTick: Int) : BaseMachineBlock(Alchemistry.catalyxSettings, name, tileClass, guiID) {
	init {
		defaultState = blockState.baseState.withProperty(FACING, EnumFacing.NORTH)
			.withProperty(STATUS, PropertyPowerStatus.OFF)
	}

	override fun createItemBlock(): Item =
		TooltipItemBlock(
			this,
			"tooltip.alchemistry.energy_requirement".translate(energyPerTick)
		)

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
		val state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)
		return state.withProperty(FACING, placer.horizontalFacing.opposite).withProperty(STATUS, PropertyPowerStatus.OFF)
	}

	@Deprecated("")
	override fun getStateFromMeta(meta: Int): IBlockState {
		val facing = when(meta) {
			in 0..2 -> EnumFacing.NORTH
			in 3..5 -> EnumFacing.SOUTH
			in 6..8 -> EnumFacing.WEST
			in 9..11 -> EnumFacing.EAST
			else -> EnumFacing.NORTH
		}
		val status = when(meta % 3) {
			0 -> PropertyPowerStatus.OFF
			1 -> PropertyPowerStatus.STANDBY
			2 -> PropertyPowerStatus.ON
			else -> PropertyPowerStatus.OFF
		}
		/* var enumfacing = EnumFacing.byIndex(meta)
		 if (enumfacing.axis == EnumFacing.Axis.Y) {
			 enumfacing = EnumFacing.NORTH
		 }
		 return this.defaultState.withProperty(FACING, enumfacing)
		 */
		return this.defaultState.withProperty(FACING, facing).withProperty(STATUS, status)
	}

	override fun getMetaFromState(state: IBlockState): Int {
		//val dir: Int = (state.getValue(FACING) as EnumFacing).index
		var sum = when(state.getValue(FACING)) {
			EnumFacing.NORTH -> 0
			EnumFacing.SOUTH -> 3
			EnumFacing.WEST -> 6
			EnumFacing.EAST -> 9
			else -> 0
		}
		sum += when(state.getValue(STATUS)) {
			PropertyPowerStatus.OFF -> 0
			PropertyPowerStatus.STANDBY -> 1
			PropertyPowerStatus.ON -> 2
			else -> 0
		}
		return sum
	}

	companion object {
		val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)
		val STATUS: PropertyEnum<PropertyPowerStatus> = PropertyEnum.create("status", PropertyPowerStatus::class.java)
		val PROPERTIES = arrayOf<IProperty<*>>(FACING, STATUS)
	}
}
