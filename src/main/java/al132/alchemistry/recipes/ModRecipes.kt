package al132.alchemistry.recipes

import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ItemElementIngot
import al132.alchemistry.items.ModItems
import al132.alchemistry.recipes.register.*
import al132.alib.utils.extensions.toStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by al132 on 1/16/2017.
 */
object ModRecipes {

    val electrolyzerRecipes = ElectrolyzerRegister.INSTANCE
    val evaporatorRecipes = EvaporatorRegister.INSTANCE
    val dissolverRecipes = DissolverRegister.INSTANCE
    val combinerRecipes = CombinerRegister.INSTANCE
    val atomizerRecipes = AtomizerRegister.INSTANCE
    val liquifierRecipes = LiquifierRegister.INSTANCE
    val fissionRecipes = FissionRegister.INSTANCE
    val fusionRecipes = FusionRegister.INSTANCE

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
        (1 until 119).filterNot { ItemElementIngot.invalidIngots.contains(it) }.forEach { i ->
            val elementName: String =
                ElementRegistry[i]!!.name[0].uppercaseChar() + ElementRegistry[i]!!.name.substring(1)
            OreDictionary.registerOre("ingot$elementName", ModItems.ingots.toStack(meta = i))
        }
    }
}