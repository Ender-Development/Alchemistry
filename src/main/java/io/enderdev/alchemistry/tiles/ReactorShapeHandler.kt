package io.enderdev.alchemistry.tiles

import al132.alib.utils.Translator
import al132.alib.utils.extensions.translate
import io.enderdev.alchemistry.ConfigHandler
import io.enderdev.alchemistry.blocks.ModBlocks
import io.enderdev.alchemistry.client.BlockHighlighter
import net.minecraft.block.Block
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.Fluid

class ReactorShapeHandler(val controller: AbstractReactorController<*>) {

    var multiblockDirection: EnumFacing? = controller.getFacing()

    val casingBlock: Block
    val glassBlock: Block
    val controllerBlock: Block
    val coreBlock: Block
    val compactEnabled: Boolean

    var failReason: () -> Pair<String, String>? = { null }
        private set
    var failPos: BlockPos? = null
        private set
    var failRed: Boolean = false
        private set

    init {
        when (controller.reactorType) {
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
        failReason = { null }
        failPos = null
        failRed = false
        multiblockDirection = controller.getFacing()?.opposite
        if (multiblockDirection == null) return false
        val corePos: BlockPos = controller.pos.offsetBack(3).offsetUp(2)
        val checkOuterCasing = getOuterCasings().all { isCasing(it) }
        if (!checkOuterCasing)
            return false

        val checkInnerCasing = getInnerCasings().all { isFilling(it) }
        if (!checkInnerCasing)
            return false

        if (!compactEnabled) {
            val other = touchesOtherReactorPart()
            if (other != null) {
                failReason = {
                    "tile.reactor.non_compact_touching".translate() to "tile.reactor.non_compact_touching_line2".translate()
                }
                failPos = other
                failRed = true
                return false
            }
        }

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
        if (!checkCore)
            return false

        // this is not perfect but it's good enough
        val checkInside = getInnerVolume().all { isNonCore(it) || isCore(it) }
        return checkInside
    }

    fun countFluid(): Map<Fluid, Int> {
        val ret = mutableMapOf<Fluid, Int>()
        getInnerVolume().forEach {
            val block = controller.world.getBlockState(it)
            if (block is Fluid)
                ret.compute(block) { _: Fluid, cnt: Int? -> (cnt ?: 0) + 1 }
        }
        return ret
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

    private fun touchesOtherReactorPart(): BlockPos? {
        val spaceAroundReactor = mutableSetOf<BlockPos>()
        spaceAroundReactor.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2),
                controller.pos.offsetRight(2).offsetUp(4)
            )
        )
        spaceAroundReactor.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(6),
                controller.pos.offsetRight(2).offsetUp(4).offsetBack(6)
            )
        )
        spaceAroundReactor.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(3).offsetBack(1),
                controller.pos.offsetLeft(3).offsetUp(4).offsetBack(5)
            )
        )
        spaceAroundReactor.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetRight(3).offsetBack(1),
                controller.pos.offsetRight(3).offsetUp(4).offsetBack(5)
            )
        )
        spaceAroundReactor.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(1).offsetUp(5),
                controller.pos.offsetRight(2).offsetBack(5).offsetUp(5)
            )
        )
        spaceAroundReactor.addAll(
            BlockPos.getAllInBox(
                controller.pos.offsetLeft(2).offsetBack(1).offsetDown(1),
                controller.pos.offsetRight(2).offsetBack(5).offsetDown(1)
            )
        )
        return spaceAroundReactor.firstOrNull {isReactorPart(it) && controller.pos != it }
    }

    private fun getInnerVolume(): Set<BlockPos> {
        val innerVolume = mutableSetOf<BlockPos>()
        val innerCorner1 = controller.pos.offsetBack(2).offsetLeft().offsetUp()
        val innerCorner2 = innerCorner1.offsetBack(2).offsetRight(2).offsetUp(2)
        innerVolume.addAll(BlockPos.getAllInBox(innerCorner1, innerCorner2))
        return innerVolume
    }

    private fun isAnything(pos: BlockPos, check: Boolean, expected: Block, red: Boolean = false): Boolean {
        if (check)
            return true

        failReason = {
            Translator.translateToLocalFormatted("tile.reactor.structure_incomplete", expected.localizedName) to
                    Translator.translateToLocalFormatted(
                        "tile.reactor.structure_incomplete_coordinates",
                        pos.x,
                        pos.y,
                        pos.z
                    )
        }
        failPos = pos
        failRed = red

        return false
    }

    private fun isAir(pos: BlockPos) = isAnything(pos, controller.world.isAirBlock(pos), Blocks.AIR, true)
    private fun isLiquid(pos: BlockPos) = controller.world.getBlockState(pos).block is BlockLiquid
    private fun isCore(pos: BlockPos) =
        isAnything(pos, controller.world.getBlockState(pos).block == coreBlock, coreBlock)

    private fun isCasing(pos: BlockPos) =
        isAnything(pos, controller.world.getBlockState(pos).block == casingBlock, casingBlock)

    private fun isFilling(pos: BlockPos): Boolean {
        val block = controller.world.getBlockState(pos).block
        return isAnything(pos, block == glassBlock || block == casingBlock, casingBlock)
    }

    private fun isReactorPart(pos: BlockPos): Boolean {
        val block = controller.world.getBlockState(pos).block
        return block == coreBlock || block == glassBlock || block == casingBlock || block == controllerBlock
    }

    private fun isNonCore(pos: BlockPos) = isAir(pos) || isLiquid(pos)

    fun highlightIncorrect() {
        if (failPos == null)
            return

        val r: Float
        val g: Float
        val b: Float
        if (failRed) {
            r = .8f
            g = .1f
            b = .1f
        } else {
            r = .1f
            g = .7f
            b = .6f
        }
        BlockHighlighter.highlightBlock(failPos!!, r, g, b, 5000)
    }

    private fun BlockPos.offsetUp(amt: Int = 1) = this.offset(EnumFacing.UP, amt)
    private fun BlockPos.offsetLeft(amt: Int = 1) = this.offset(multiblockDirection!!.rotateY(), amt)
    private fun BlockPos.offsetRight(amt: Int = 1) = this.offset(multiblockDirection!!.rotateY(), -amt)
    private fun BlockPos.offsetBack(amt: Int = 1) = this.offset(multiblockDirection!!, amt)
    private fun BlockPos.offsetForward(amt: Int = 1) = this.offset(multiblockDirection!!, -amt)
    private fun BlockPos.offsetDown(amt: Int = 1) = this.offset(EnumFacing.DOWN, amt)
}