package al132.alchemistry.tiles

import al132.alchemistry.ConfigHandler
import al132.alchemistry.blocks.ModBlocks
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.Fluid

class ReactorShapeHandler(val controller: AbstractReactorController<*>, reactorType: ReactorType) {

    var multiblockDirection: EnumFacing? = controller.getFacing()

    var casingBlock: Block? = null
    var glassBlock: Block? = null
    var controllerBlock: Block? = null
    var coreBlock: Block? = null
    var compactEnabled: Boolean = false

    init {
        when (reactorType) {
            ReactorType.FUSION -> {
                casingBlock = ModBlocks.fusionCasing
                glassBlock = ModBlocks.fusionGlass
                controllerBlock = ModBlocks.fusionController
                coreBlock = ModBlocks.fusionCore
                compactEnabled = ConfigHandler.FUSION.compactFusionReactor
            }

            ReactorType.FISSION -> {
                casingBlock = ModBlocks.fissionCasing
                glassBlock = ModBlocks.fissionGlass
                controllerBlock = ModBlocks.fissionController
                coreBlock = ModBlocks.fissionCore
                compactEnabled = ConfigHandler.FISSION.compactFissionReactor
            }
        }
    }

    fun validate(): Boolean {
        multiblockDirection = controller.getFacing()?.opposite
        if (multiblockDirection == null) return false
        val corePos: BlockPos = controller.pos.offsetBack(3).offsetUp(2)
        val checkOuterCasing = getOuterCasings().all { isCasing(it) }
        val checkInnerCasing = getInnerCasings().all { isFilling(it) }
        val checkCompact = compactEnabled || getOutside() == 0
        val checkCore = (getCoreZ(corePos).all { isCore(it) }
                    && isNonCore(corePos.offsetForward())
                    && isNonCore(corePos.offsetBack())
                    && isNonCore(corePos.offsetLeft())
                    && isNonCore(corePos.offsetRight()))
                || (getCoreX(corePos).all { isCore(it) }
                    && isNonCore(corePos.offsetUp())
                    && isNonCore(corePos.offsetDown())
                    && isNonCore(corePos.offsetForward())
                    && isNonCore(corePos.offsetBack()))
                || (getCoreY(corePos).all { isCore(it) }
                    && isNonCore(corePos.offsetLeft())
                    && isNonCore(corePos.offsetRight())
                    && isNonCore(corePos.offsetUp())
                    && isNonCore(corePos.offsetDown()))
        return checkOuterCasing && checkInnerCasing && checkCompact && checkCore
    }

    fun countFluid(fluid: Fluid): Int {
        return getInnerVolume().count { controller.world.getBlockState(it).block == fluid.block }
    }

    private fun getOuterCasings(): Set<BlockPos> {
        val outerCasing = mutableSetOf<BlockPos>()
        // 4 vertical edges
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(1),
                controller.pos.offsetLeft(2).offsetBack(1).offsetUp(4)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetRight(2).offsetBack(1),
                controller.pos.offsetRight(2).offsetBack(1).offsetUp(4)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(5),
                controller.pos.offsetLeft(2).offsetBack(5).offsetUp(4)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetRight(2).offsetBack(5),
                controller.pos.offsetRight(2).offsetBack(5).offsetUp(4)
            )
        )
        // 4 lower horizontal edges (decreased by 1 on each side to avoid double counting)
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(1).offsetBack(1),
                controller.pos.offsetRight(1).offsetBack(1)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(1).offsetBack(5),
                controller.pos.offsetRight(1).offsetBack(5)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(2),
                controller.pos.offsetLeft(2).offsetBack(4)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetRight(2).offsetBack(2),
                controller.pos.offsetRight(2).offsetBack(4)
            )
        )
        // 4 upper horizontal edges (decreased by 1 on each side to avoid double counting)
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(1).offsetBack(1).offsetUp(4),
                controller.pos.offsetRight(1).offsetBack(1).offsetUp(4)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(1).offsetBack(5).offsetUp(4),
                controller.pos.offsetRight(1).offsetBack(5).offsetUp(4)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(2).offsetUp(4),
                controller.pos.offsetLeft(2).offsetBack(4).offsetUp(4)
            )
        )
        outerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetRight(2).offsetBack(2).offsetUp(4),
                controller.pos.offsetRight(2).offsetBack(4).offsetUp(4)
            )
        )
        return outerCasing
    }

    private fun getInnerCasings(): Set<BlockPos> {
        val innerCasing = mutableSetOf<BlockPos>()
        // lower and upper horizontal panes decreased by 1 on each side to only check the inner casing
        innerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(1).offsetBack(2),
                controller.pos.offsetRight(1).offsetBack(4)
            )
        )
        innerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(1).offsetBack(2).offsetUp(4),
                controller.pos.offsetRight(1).offsetBack(4).offsetUp(4)
            )
        )
        // vertical panes decreased by 1 on each side to only check the inner casing
        innerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(2).offsetUp(1),
                controller.pos.offsetLeft(2).offsetBack(4).offsetUp(3)
            )
        )
        innerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetRight(2).offsetBack(2).offsetUp(1),
                controller.pos.offsetRight(2).offsetBack(4).offsetUp(3)
            )
        )
        innerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(1).offsetUp(1),
                controller.pos.offsetRight(2).offsetBack(1).offsetUp(3)
            )
        )
        innerCasing.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(5).offsetUp(1),
                controller.pos.offsetRight(2).offsetBack(5).offsetUp(3)
            )
        )
        return innerCasing
    }

    private fun getCoreZ(corePos: BlockPos): Set<BlockPos> {
        val core = mutableSetOf<BlockPos>()
        core.addAll(
            BlockPos.getAllInBox(
                corePos.offsetUp(),
                corePos.offsetDown()
            )
        )
        return core
    }

    private fun getCoreX(corePos: BlockPos): Set<BlockPos> {
        val core = mutableSetOf<BlockPos>()
        core.addAll(
            BlockPos.getAllInBox(
                corePos.offsetLeft(),
                corePos.offsetRight()
            )
        )
        return core
    }

    private fun getCoreY(corePos: BlockPos): Set<BlockPos> {
        val core = mutableSetOf<BlockPos>()
        core.addAll(
            BlockPos.getAllInBox(
                corePos.offsetForward(),
                corePos.offsetBack()
            )
        )
        return core
    }

    private fun getOutside(): Int {
        val outsideCorner1 = controller.pos.offsetLeft(3).offsetDown()
        val outsideCorner2 = outsideCorner1.offsetRight(6).offsetUp(6).offsetBack(6)
        val borderingParts = BlockPos.getAllInBox(outsideCorner1, outsideCorner2).filter {
            var sharedAxes = 0
            if (it.x == outsideCorner1.x || it.x == outsideCorner2.x) sharedAxes++
            if (it.y == outsideCorner1.y || it.y == outsideCorner2.y) sharedAxes++
            if (it.z == outsideCorner1.z || it.z == outsideCorner2.z) sharedAxes++
            sharedAxes >= 1
        }.filterNot(controller.pos::equals)
            .count(this::isReactorPart)
        return borderingParts
    }

    private fun getInnerVolume(): Set<BlockPos> {
        val innerVolume = mutableSetOf<BlockPos>()
        val innerCorner1 = controller.pos.offsetBack(2).offsetLeft().offsetUp()
        val innerCorner2 = innerCorner1.offsetBack(2).offsetRight(2).offsetUp(2)
        innerVolume.addAll(BlockPos.getAllInBox(innerCorner1, innerCorner2))
        return innerVolume
    }

    private fun isAir(pos: BlockPos): Boolean = controller.world.isAirBlock(pos)
    private fun isLiquid(pos: BlockPos): Boolean = controller.world.getBlockState(pos).block is BlockLiquid
    private fun isCore(pos: BlockPos): Boolean = (controller.world.getBlockState(pos).block == coreBlock)
    private fun isCasing(pos: BlockPos): Boolean = (controller.world.getBlockState(pos).block == casingBlock)
    private fun isFilling(pos: BlockPos): Boolean =
        (controller.world.getBlockState(pos).block == glassBlock) || isCasing(pos)

    private fun isReactorPart(pos: BlockPos): Boolean {
        return isCore(pos) || isFilling(pos) || (controller.world.getBlockState(pos).block == controllerBlock)
    }

    private fun isNonCore(pos: BlockPos): Boolean {
        return isAir(pos) || isLiquid(pos)
    }

    private fun BlockPos.offsetUp(amt: Int = 1) = this.offset(EnumFacing.UP, amt)
    private fun BlockPos.offsetLeft(amt: Int = 1) = this.offset(multiblockDirection!!.rotateY(), amt)
    private fun BlockPos.offsetRight(amt: Int = 1) = this.offset(multiblockDirection!!.rotateY(), -1 * amt)
    private fun BlockPos.offsetBack(amt: Int = 1) = this.offset(multiblockDirection!!, amt)
    private fun BlockPos.offsetForward(amt: Int = 1) = this.offset(multiblockDirection!!, -1 * amt)
    private fun BlockPos.offsetDown(amt: Int = 1) = this.offset(EnumFacing.DOWN, amt)
}