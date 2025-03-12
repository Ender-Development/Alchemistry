package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.blocks.machine.FissionControllerBlock
import io.enderdev.alchemistry.blocks.machine.FusionControllerBlock
import io.enderdev.alchemistry.recipes.IRecipe
import io.enderdev.alchemistry.recipes.register.AbstractRecipeRegister
import al132.alib.tiles.IEnergyTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import kotlin.math.floor

abstract class AbstractReactorController<T: IRecipe>(val reactorType: ReactorType, recipeRegister: AbstractRecipeRegister<T>) : AbstractMachine<T>(recipeRegister), IEnergyTile {

    val shapeHandler = ReactorShapeHandler(this, reactorType)
    var fluidModifiers = mutableMapOf<Fluid, List<Double>>() //List<Productivity, Speed, Energy>
    var productivityModifier: Double = 1.0
    var speedModifier: Double = 1.0
    var energyModifier: Double = 1.0
    var isMultiblockValid: Boolean = false
    var checkMultiblockTicks: Int = 0

    fun getFacing(): EnumFacing? {
        val state = this.world?.getBlockState(this.pos)
        return when (reactorType) {
            ReactorType.FISSION -> state?.getValue(FissionControllerBlock.Companion.FACING)
            ReactorType.FUSION -> state?.getValue(FusionControllerBlock.Companion.FACING)
        }
    }

    fun updateMultiblock() {
        isMultiblockValid = validateMultiblock()
    }

    fun validateMultiblock(): Boolean = shapeHandler.validate()

    fun updateModifiers() {
        val updateModifier: List<List<Double>> = fluidModifiers.map { fluid ->
            val valueList = fluid.value.toMutableList()
            val count = shapeHandler.countFluid(fluid.key)
            valueList.map { it.times(count) }
        }
        productivityModifier = updateModifier.sumOf { it[0] }
        speedModifier = updateModifier.sumOf { it[1] }
        energyModifier = updateModifier.sumOf { it[2] }
    }

    fun loadConfig(configValue: Array<String>) {
        fluidModifiers.clear()
        configValue.forEach {
            val split = it.split(";")
            if (split.size != 4) return
            val fluid = FluidRegistry.getFluid(split[0])
            if (fluid == null) return
            val productivity = split[1].toDouble()
            val speed = split[2].toDouble()
            val energy = split[3].toDouble()
            fluidModifiers[fluid] = listOfNotNull(productivity, speed, energy)
        }
    }

    fun getModifiedProcessTime(default: Int): Int {
        return floor(default * (1 - speedModifier)).toInt()
    }

    fun getModifiedEnergyCost(default: Int): Int {
        return floor(default * (1 + energyModifier)).toInt()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return if (shapeHandler.validate()) super.hasCapability(capability, facing)
        else false
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (shapeHandler.validate()) super.getCapability(capability, facing)
        else null
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setInteger("ProgressTicks", progressTicks)
        compound.setDouble("speedModifier", speedModifier)
        compound.setDouble("productivityModifier", productivityModifier)
        compound.setDouble("energyModifier", energyModifier)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.progressTicks = compound.getInteger("ProgressTicks")
        this.speedModifier = compound.getDouble("speedModifier")
        this.productivityModifier = compound.getDouble("productivityModifier")
        this.energyModifier = compound.getDouble("energyModifier")
        this.updateMultiblock()
    }
}