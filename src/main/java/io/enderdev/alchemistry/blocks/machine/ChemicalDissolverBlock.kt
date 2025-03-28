package io.enderdev.alchemistry.blocks.machine

import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.items.TooltipItemBlock
import io.enderdev.alchemistry.utils.extensions.translate
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent

class ChemicalDissolverBlock(name: String,
                            tileClass: Class<out TileEntity>,
                            guiID: Int)
    : BaseMachineBlock(name, tileClass, guiID) {

    override fun registerItemBlock(event: RegistryEvent.Register<Item>){
        event.registry.register(
            TooltipItemBlock(
                this,
                "tooltip.alchemistry.energy_requirement".translate(ConfigHandler.DISSOLVER.energyPerTick)
            )
                .setRegistryName(this.registryName))
    }

    val boundingBox = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0)

    @Deprecated("")
    override fun getRenderType(state: IBlockState): EnumBlockRenderType = EnumBlockRenderType.MODEL

    @Deprecated("")
    override fun isOpaqueCube(state: IBlockState) = false

    @Deprecated("")
    override fun isFullCube(state: IBlockState) = false

    @Deprecated("")
    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = boundingBox

    @Deprecated("")
    override fun addCollisionBoxToList(state: IBlockState,
                                       worldIn: World,
                                       pos: BlockPos,
                                       entityBox: AxisAlignedBB,
                                       collidingBoxes: List<AxisAlignedBB>,
                                       entityIn: Entity?, mysteryboolean: Boolean) {

        @Suppress("DEPRECATION")
        addCollisionBoxToList(pos, entityBox, collidingBoxes, boundingBox)
    }
}