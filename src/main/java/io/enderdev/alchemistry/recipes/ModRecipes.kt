package io.enderdev.alchemistry.recipes

import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.items.ItemElementIngot
import io.enderdev.alchemistry.items.ModItems
import io.enderdev.alchemistry.recipes.register.*
import io.enderdev.catalyx.utils.extensions.toStack
import net.minecraftforge.oredict.OreDictionary

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
		(1..118).forEach { i ->
			if(!ItemElementIngot.Companion.invalidIngots.contains(i)) {
				val elementName: String = ElementRegistry[i]!!.name.replaceFirstChar(Char::uppercaseChar)
				OreDictionary.registerOre("ingot$elementName", ModItems.ingots.toStack(meta = i))
			}
		}
	}
}
