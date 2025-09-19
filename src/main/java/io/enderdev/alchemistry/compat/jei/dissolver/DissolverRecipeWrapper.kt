package io.enderdev.alchemistry.compat.jei.dissolver

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.compat.jei.AlchemistryRecipeWrapper
import io.enderdev.alchemistry.recipes.DissolverRecipe
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.ingredients.VanillaTypes
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import org.ender_development.catalyx.utils.extensions.translate
import java.awt.Color

class DissolverRecipeWrapper(recipe: DissolverRecipe) : AlchemistryRecipeWrapper<DissolverRecipe>(recipe) {

	fun formatProbability(probability: Double): String {
		var prob = probability
		if(recipe.outputs.relativeProbability)
			prob *= 100

		return "${Alchemistry.DECIMAL_FORMAT.format(prob)}%"
	}

	override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
		val fontRenderer: FontRenderer = minecraft.fontRenderer

		var y = 50
		for(index in recipe.outputs.set.indices) {
			val text = formatProbability(recipe.outputs.probabilityAtIndex(index))
			fontRenderer.drawString(text, 0, y, Color.BLACK.rgb)
			y += 18
		}

		val probabilityType = "jei.dissolver.${if(recipe.outputs.relativeProbability) "relative" else "absolute"}".translate()

		fontRenderer.drawString("jei.dissolver.type".translate(probabilityType), 0, 4, Color.BLACK.rgb)
		fontRenderer.drawString("jei.dissolver.rolls".translate(recipe.outputs.rolls), 0, 16, Color.BLACK.rgb)
	}

	override fun getIngredients(ingredients: IIngredients) {
		ingredients.setInputs(VanillaTypes.ITEM, recipe.inputs)
		ingredients.setOutputs(VanillaTypes.ITEM, recipe.outputs.toStackList())
	}
}
