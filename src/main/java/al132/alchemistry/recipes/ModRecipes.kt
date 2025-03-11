package al132.alchemistry.recipes

import al132.alchemistry.chemistry.CompoundRegistry
import al132.alchemistry.chemistry.ElementRegistry
import al132.alchemistry.items.ItemElementIngot
import al132.alchemistry.items.ModItems
import al132.alchemistry.recipes.register.*
import al132.alib.utils.extensions.toStack
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.IFuelHandler
import net.minecraftforge.fml.common.registry.GameRegistry
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

    fun init() {
        initFuelHandler()
        electrolyzerRecipes.registerRecipes()
        evaporatorRecipes.registerRecipes()
        dissolverRecipes.registerRecipes() // before combiner, so combiner can use reversible recipes
        combinerRecipes.registerRecipes()
        atomizerRecipes.registerRecipes() // before liquifier, so liquifier can use reversible recipes
        liquifierRecipes.registerRecipes()
        fissionRecipes.registerRecipes()
    }

    fun initOredict() {
        (1 until 119).filterNot { ItemElementIngot.invalidIngots.contains(it) }.forEach { i ->
            val elementName: String =
                ElementRegistry[i]!!.name[1].uppercaseChar() + ElementRegistry[i]!!.name.substring(2)
            OreDictionary.registerOre("ingot$elementName", ModItems.ingots.toStack(meta = i))
        }
    }

    fun initFuelHandler() {
        val fuelHandler = object : IFuelHandler {
            override fun getBurnTime(fuel: ItemStack?): Int {
                if (fuel != null) {
                    if (fuel.item == ModItems.elements) {
                        return when (fuel.itemDamage) {
                            ElementRegistry["hydrogen"]?.meta -> 20
                            ElementRegistry["carbon"]?.meta -> 200
                            else -> 0
                        }
                    } else if (fuel.item == ModItems.compounds) {
                        return when (fuel.itemDamage) {
                            CompoundRegistry["methane"]?.meta -> ((1 * 200) + (4 * 20))
                            CompoundRegistry["ethane"]?.meta -> ((2 * 200) + (6 * 20))
                            CompoundRegistry["propane"]?.meta -> ((3 * 200) + (8 * 20))
                            CompoundRegistry["butane"]?.meta -> ((4 * 200) + (10 * 20))
                            CompoundRegistry["pentane"]?.meta -> ((5 * 200) + (12 * 20))
                            CompoundRegistry["hexane"]?.meta -> ((6 * 200) + (14 * 20))
                            else -> 0
                        }
                    }
                }
                return 0
            }
        }
        GameRegistry.registerFuelHandler(fuelHandler)
    }
}