package al132.alchemistry.tiles

import al132.alchemistry.blocks.FissionControllerBlock
import al132.alchemistry.blocks.FusionControllerBlock
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

abstract class AbstractReactorController(val reactorType: ReactorType) : TileBase() {

    val shapeHandler = ReactorShapeHandler(this, reactorType)
    var isMultiblockValid: Boolean = false
    var checkMultiblockTicks: Int = 0
    var progressTicks = 0

    abstract fun refreshRecipe()
    abstract fun canProcess(): Boolean
    abstract fun process()

    fun getFacing(): EnumFacing? {
        val state = this.world?.getBlockState(this.pos)
        return when (reactorType) {
            ReactorType.FISSION -> state?.getValue(FissionControllerBlock.FACING)
            ReactorType.FUSION -> state?.getValue(FusionControllerBlock.FACING)
        }
    }

    fun updateMultiblock() {
        isMultiblockValid = validateMultiblock()
    }

    fun validateMultiblock(): Boolean = shapeHandler.validate()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (shapeHandler.validate()) super.hasCapability(capability, facing)
        else false
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (shapeHandler.validate()) super.getCapability(capability, facing)
        else null
    }
}