package io.enderdev.alchemistry.tiles

import io.enderdev.alchemistry.recipes.IRecipe
import io.enderdev.alchemistry.recipes.register.AbstractRecipeRegister
import al132.alib.tiles.IGuiTile
import al132.alib.tiles.IItemTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable

abstract class AbstractMachine<T : IRecipe>(recipeRegister: AbstractRecipeRegister<T>) : TileBase(), ITickable,
    IGuiTile, IItemTile {
    val recipeRegister: List<T> = recipeRegister.recipes

    abstract val recipeTime: Int
    abstract val energyPerTick : Int

    var progressTicks: Int = 0
    var isPaused: Boolean = false
    var currentRecipe: T? = null

    /**
     * Update the stored recipe variable.
     * Used in init and readFromNBT.
     */
    abstract fun updateRecipe()

    /**
     * Fired when the machine finishes a recipe.
     * Used for consuming inputs and producing outputs.
     */
    abstract fun onProcessComplete()

    /**
     * Fired every tick the machine is active.
     * Used for consuming energy, etc.
     */
    abstract fun onWorkTick()

    /**
     * Check if there is anything present to process.
     */
    abstract fun shouldTick(): Boolean

    /**
     * Check if the machine should process the current recipe.
     */
    abstract fun shouldProcess(): Boolean

    open fun onIdleTick() {
        updateRecipe()
    }

    override fun update() {
        if (world.isRemote) return
        markDirtyGUIEvery(5)

        if (isPaused) return
        if (shouldTick()) {
            onIdleTick()
            if (currentRecipe != null && shouldProcess()) {
                onWorkTick()
                if (progressTicks >= recipeTime) {
                    onProcessComplete()
                    progressTicks = 0
                } else {
                    progressTicks++
                }
            } else {
                progressTicks = 0
            }
        } else {
            progressTicks = 0
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        compound.setBoolean("IsPaused", isPaused)
        compound.setInteger("ProgressTicks", progressTicks)
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        this.isPaused = compound.getBoolean("IsPaused")
        this.progressTicks = compound.getInteger("ProgressTicks")
    }
}