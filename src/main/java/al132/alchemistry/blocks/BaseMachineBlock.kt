package al132.alchemistry.blocks

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import kotlin.math.roundToInt

open class BaseMachineBlock(name: String, tileClass: Class<out TileEntity>, guiID: Int) : BaseTileBlock(name, tileClass, guiID) {
    @Deprecated("")
    override fun hasComparatorInputOverride(state: IBlockState): Boolean = true

    @Deprecated("")
    override fun getComparatorInputOverride(state: IBlockState, world: World, pos: BlockPos): Int {
        val te = world.getTileEntity(pos)
        if(te == null)
            return 0

        val cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        if(cap == null)
            return 0

        val slots = cap.slots
        var itemCount = 0
        (0 until slots).forEach {
            val item = cap.getStackInSlot(it)
            itemCount += item.count
        }

        if(itemCount == 0)
            return 0

        return (itemCount.toFloat() / (slots * 64) * 15).roundToInt()
    }
}