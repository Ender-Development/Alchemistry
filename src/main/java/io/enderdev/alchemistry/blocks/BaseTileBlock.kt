package io.enderdev.alchemistry.blocks

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.tiles.TileBase
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry


open class BaseTileBlock(name: String, var tileClass: Class<out TileEntity>, val guiID: Int) : BaseBlock(name), ITileEntityProvider {
    init {
        GameRegistry.registerTileEntity(tileClass, ResourceLocation(Tags.MOD_ID, name))
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity = tileClass.newInstance()

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(!world.isRemote) {
            val tile = world.getTileEntity(pos)
            if(tile is TileBase && !tile.onBlockActivated(world, pos,state, player, hand, facing, hitX, hitY, hitZ))
                player.openGui(Alchemistry, guiID, world, pos.x, pos.y, pos.z)
        }
        return true
    }

    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer, willHarvest: Boolean): Boolean {
        return if(willHarvest) true else super.removedByPlayer(state, world, pos, player, false)
    }

    override fun harvestBlock(world: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {
        super.harvestBlock(world, player, pos, state, te, stack)
        world.setBlockToAir(pos)
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        val item = Item.getItemFromBlock(this)
        if (item != Items.AIR) {
            drops.add(ItemStack(item, 1, damageDropped(state)).apply {
                this.tagCompound = world.getTileEntity(pos)?.updateTag
                tagCompound?.removeTag("x")
                tagCompound?.removeTag("y")
                tagCompound?.removeTag("z")
            })
            val droppedItem: ItemStack? = drops.firstOrNull { it.item == item }
            val tag = droppedItem?.tagCompound
            tag?.apply {
                if (this.hasKey("id")) removeTag("id")
                if (this.hasKey("input")) {
                    val input = this.getCompoundTag("input")
                    if (input.hasKey("Size") && input.getInteger("Size") == 0) {
                        this.removeTag("input")
                    }
                    if (input.getTagList("Items", 10).isEmpty) {
                        this.removeTag("input")
                    }
                }
                if (this.hasKey("ProgressTicks") && this.getInteger("ProgressTicks") == 0) {
                    this.removeTag("ProgressTicks")
                }
                if (this.hasKey("output")) {
                    val output = this.getCompoundTag("output")
                    if (output.hasKey("Size") && output.getInteger("Size") == 0) {
                        this.removeTag("output")
                    }
                    if (output.getTagList("Items", 10).isEmpty) {
                        this.removeTag("output")
                    }
                }
                if (this.hasKey("InputTankNBT")) {
                    val inputTank = this.getCompoundTag("InputTankNBT")
                    if (inputTank.hasKey("Empty")) this.removeTag("InputTankNBT")
                }
                if (this.hasKey("OutputTankNBT")) {
                    val inputTank = this.getCompoundTag("OutputTankNBT")
                    if (inputTank.hasKey("Empty")) this.removeTag("OutputTankNBT")
                }
                if (this.hasKey("EnergyStored") && this.getInteger("EnergyStored") == 0) {
                    this.removeTag("EnergyStored")
                }
                if (this.hasKey("Owner")) this.removeTag("Owner")
            }
            if (tag != null && tag.size == 0) droppedItem.tagCompound = null
        }
    }

    override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) {
        val tile = world.getTileEntity(pos)
        if(tile is TileBase) {
            stack.tagCompound?.setInteger("x", pos.x)
            stack.tagCompound?.setInteger("y", pos.y)
            stack.tagCompound?.setInteger("z", pos.z)
            tile.markDirtyClient()
        }
    }
}