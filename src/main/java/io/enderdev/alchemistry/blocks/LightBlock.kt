package io.enderdev.alchemistry.blocks

import io.enderdev.alchemistry.Alchemistry
import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import org.ender_development.catalyx.blocks.BaseBlock

class LightBlock(name: String) : BaseBlock(Alchemistry, name) {
	init {
		lightValue = 15
		soundType = SoundType.GLASS
	}

	@Deprecated("")
	override fun isOpaqueCube(state: IBlockState) = false

	override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT

	@Deprecated("")
	override fun shouldSideBeRendered(state: IBlockState, access: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
		return access.getBlockState(pos.add(side.directionVec)).block !is LightBlock
	}
}
