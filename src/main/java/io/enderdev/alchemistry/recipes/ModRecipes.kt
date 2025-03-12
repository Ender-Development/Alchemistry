package io.enderdev.alchemistry.recipes

import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.items.ItemElementIngot
import io.enderdev.alchemistry.items.ModItems
import al132.alib.utils.extensions.toStack
import io.enderdev.alchemistry.recipes.register.AtomizerRegister
import io.enderdev.alchemistry.recipes.register.CombinerRegister
import io.enderdev.alchemistry.recipes.register.DissolverRegister
import io.enderdev.alchemistry.recipes.register.ElectrolyzerRegister
import io.enderdev.alchemistry.recipes.register.EvaporatorRegister
import io.enderdev.alchemistry.recipes.register.FissionRegister
import io.enderdev.alchemistry.recipes.register.FusionRegister
import io.enderdev.alchemistry.recipes.register.LiquifierRegister
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by al132 on 1/16/2017.
 */
object ModRecipes {

    val electrolyzerRecipes = ElectrolyzerRegister.Companion.INSTANCE
    val evaporatorRecipes = EvaporatorRegister.Companion.INSTANCE
    val dissolverRecipes = DissolverRegister.Companion.INSTANCE
    val combinerRecipes = CombinerRegister.Companion.INSTANCE
    val atomizerRecipes = AtomizerRegister.Companion.INSTANCE
    val liquifierRecipes = LiquifierRegister.Companion.INSTANCE
    val fissionRecipes = FissionRegister.Companion.INSTANCE
    val fusionRecipes = FusionRegister.Companion.INSTANCE

    fun init() {
        electrolyzerRecipes.registerRecipes()
        evaporatorRecipes.registerRecipes()
        dissolverRecipes.registerRecipes() // before combiner, so combiner can use reversible recipes
        combinerRecipes.registerRecipes()
        atomizerRecipes.registerRecipes() // before liquifier, so liquifier can use reversible recipes
        liquifierRecipes.registerRecipes()
        fissionRecipes.registerRecipes()
        fusionRecipes.registerRecipes()
    }

    fun initOredict() {
        (1 until 119).filterNot { ItemElementIngot.Companion.invalidIngots.contains(it) }.forEach { i ->
            val elementName: String =
                ElementRegistry[i]!!.name[0].uppercaseChar() + ElementRegistry[i]!!.name.substring(1)
            OreDictionary.registerOre("ingot$elementName", ModItems.ingots.toStack(meta = i))
        }
    }
}