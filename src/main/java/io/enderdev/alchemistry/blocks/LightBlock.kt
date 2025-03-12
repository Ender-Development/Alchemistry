package io.enderdev.alchemistry.blocks

import net.minecraft.block.SoundType
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class LightBlock(name: String) : BaseBlock(name){
    init {
        this.setLightLevel(1.0f)
        this.soundType = SoundType.GLASS
    }

    override fun isOpaqueCube(state: IBlockState) = false

    override fun getRenderLayer() = BlockRenderLayer.TRANSLUCENT

    @Deprecated("")
    override fun shouldSideBeRendered(state: IBlockState, access: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return access.getBlockState(pos.add(side.directionVec)).block !is LightBlock
    }
}