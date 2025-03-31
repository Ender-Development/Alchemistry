package io.enderdev.alchemistry.blocks

import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class GlassBlock(name: String) : BaseBlock(name) {
	init {
		this.soundType = SoundType.GLASS
	}

	@Deprecated("")
	override fun isOpaqueCube(state: IBlockState) = false

	@Deprecated("")
	override fun shouldSideBeRendered(state: IBlockState, access: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
		return access.getBlockState(pos.add(side.directionVec)).block != this
	}

	override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT
}
