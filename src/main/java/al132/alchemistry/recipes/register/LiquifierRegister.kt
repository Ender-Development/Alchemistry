package al132.alchemistry.recipes.register

import al132.alchemistry.recipes.LiquifierRecipe

class LiquifierRegister: AbstractRecipeRegister<LiquifierRecipe>() {
    companion object {
        val INSTANCE = LiquifierRegister()
    }

    override fun registerRecipes() {
        AtomizerRegister.INSTANCE.recipes.filter { it.reversible }.forEach {
            recipes.add(LiquifierRecipe(it.output.copy(), it.input.copy()))
        }
    }
}