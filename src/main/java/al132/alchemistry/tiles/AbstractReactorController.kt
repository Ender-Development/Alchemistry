package al132.alchemistry.tiles

import al132.alchemistry.blocks.FissionControllerBlock
import al132.alchemistry.blocks.FusionControllerBlock
import al132.alib.tiles.IEnergyTile
import net.minecraft.util.EnumFacing
import net.minecraft.util.Tuple
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import kotlin.math.floor

abstract class AbstractReactorController(val reactorType: ReactorType) : TileBase(), IEnergyTile {

    val shapeHandler = ReactorShapeHandler(this, reactorType)
    var fluidModifiers = mutableMapOf<Fluid, Tuple<Double, Double>>() //Tuple<Productivity, Speed>
    var productivityModifier: Double = 1.0
    var speedModifier: Double = 1.0
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

    fun productivityModifier(): Double {
        return fluidModifiers.map { fluid -> fluid.value.first.times(shapeHandler.countFluid(fluid.key)) }.sum()
    }

    fun speedModifier(): Double {
        return fluidModifiers.map { fluid -> fluid.value.second.times(shapeHandler.countFluid(fluid.key)) }.sum()
    }

    fun loadConfig(configValue: Array<String>) {
        fluidModifiers.clear()
        configValue.forEach {
            val split = it.split(";")
            if (split.size != 3) return
            val fluid = FluidRegistry.getFluid(split[0])
            if (fluid == null) return
            val productivity = split[1].toDouble()
            val speed = split[2].toDouble()
            fluidModifiers[fluid] = Tuple(productivity, speed)
        }
    }

    fun getModifiedProcessTime(default: Int): Int {
        return floor(default + default * (1 - speedModifier)).toInt()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (shapeHandler.validate()) super.hasCapability(capability, facing)
        else false
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (shapeHandler.validate()) super.getCapability(capability, facing)
        else null
    }
}