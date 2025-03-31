package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.blocks.machine.ReactorControllerBlock
import io.enderdev.alchemistry.client.BlockHighlighter
import io.enderdev.alchemistry.recipes.IRecipe
import io.enderdev.alchemistry.recipes.register.AbstractRecipeRegister
import io.enderdev.alchemistry.tiles.tags.IEnergyTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

abstract class AbstractReactorController<T: IRecipe>(val reactorType: ReactorType, recipeRegister: AbstractRecipeRegister<T>) : AbstractMachine<T>(recipeRegister), IEnergyTile {
    val shapeHandler = ReactorShapeHandler(this)
    var fluidModifiers = mutableMapOf<Fluid, Modifier>()
    var currentModifier = Modifier(.0, .0, .0)
    var isMultiblockValid = false
    var checkMultiblockTicks = 0

    fun getFacing() = world?.getBlockState(pos)?.getValue(ReactorControllerBlock.Companion.FACING)

    fun updateMultiblock() {
        val highlight = !isMultiblockValid && world?.isRemote == true && shapeHandler.failPos != null && BlockHighlighter.pos == shapeHandler.failPos

        isMultiblockValid = validateMultiblock()

        if(!isMultiblockValid && highlight && BlockHighlighter.pos != shapeHandler.failPos)
            shapeHandler.highlightIncorrect()
    }

    fun validateMultiblock() = shapeHandler.validate()

    fun updateModifiers() {
        val fluids = shapeHandler.countFluid()
        currentModifier.zero()
        fluids.map { (fluid: Fluid, cnt: Int) ->
            fluidModifiers[fluid]?.let { (productivity, speed, energy) ->
                currentModifier.productivity += productivity * cnt
                currentModifier.speed += speed * cnt
                currentModifier.energy += energy * cnt
            }
        }
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
            fluidModifiers[fluid] = Modifier(productivity, speed, energy)
        }
    }

    fun getModifiedProcessTime(default: Int) = (default * (1 - currentModifier.speed)).toInt()

    fun getModifiedEnergyCost(default: Int) = (default * (1 + currentModifier.energy)).toInt()

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
        if(isMultiblockValid) super.hasCapability(capability, facing) else false

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?) =
        if(isMultiblockValid) super.getCapability(capability, facing) else null

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setInteger("ProgressTicks", progressTicks)
        compound.setDouble("productivityModifier", currentModifier.productivity)
        compound.setDouble("speedModifier", currentModifier.speed)
        compound.setDouble("energyModifier", currentModifier.energy)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        progressTicks = compound.getInteger("ProgressTicks")
        currentModifier = Modifier(
            compound.getDouble("productivityModifier"),
            compound.getDouble("speedModifier"),
            compound.getDouble("energyModifier")
        )
        updateMultiblock()
    }

    data class Modifier(var productivity: Double, var speed: Double, var energy: Double) {
        fun zero() {
            productivity = .0
            speed = .0
            energy = .0
        }
    }
}