package io.enderdev.alchemistry.recipes

data class FusionRecipe(val inputMeta1: Int, val inputMeta2: Int, val outputMeta: Int) : IRecipe {
	constructor(inputMeta1: Int, inputMeta2: Int) : this(inputMeta1, inputMeta2, inputMeta1 + inputMeta2)
}
