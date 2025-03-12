package io.enderdev.alchemistry.compat.jei

import io.enderdev.alchemistry.Reference
import io.enderdev.alchemistry.blocks.ModBlocks
import io.enderdev.alchemistry.client.container.ContainerChemicalDissolver
import io.enderdev.alchemistry.client.container.ContainerElectrolyzer
import io.enderdev.alchemistry.client.container.ContainerFissionController
import io.enderdev.alchemistry.client.container.ContainerFusionController
import io.enderdev.alchemistry.client.container.ContainerLiquifier
import io.enderdev.alchemistry.client.gui.GuiAtomizer
import io.enderdev.alchemistry.client.gui.GuiChemicalCombiner
import io.enderdev.alchemistry.client.gui.GuiChemicalDissolver
import io.enderdev.alchemistry.client.gui.GuiElectrolyzer
import io.enderdev.alchemistry.client.gui.GuiEvaporator
import io.enderdev.alchemistry.client.gui.GuiFissionController
import io.enderdev.alchemistry.client.gui.GuiFusionController
import io.enderdev.alchemistry.client.gui.GuiLiquifier
import io.enderdev.alchemistry.compat.jei.atomizer.AtomizerRecipeCategory
import io.enderdev.alchemistry.compat.jei.atomizer.AtomizerRecipeWrapper
import io.enderdev.alchemistry.compat.jei.combiner.CombinerRecipeCategory
import io.enderdev.alchemistry.compat.jei.combiner.CombinerRecipeWrapper
import io.enderdev.alchemistry.compat.jei.combiner.CombinerTransferHandler
import io.enderdev.alchemistry.compat.jei.dissolver.DissolverRecipeCategory
import io.enderdev.alchemistry.compat.jei.dissolver.DissolverRecipeWrapper
import io.enderdev.alchemistry.compat.jei.electrolyzer.ElectrolyzerRecipeCategory
import io.enderdev.alchemistry.compat.jei.electrolyzer.ElectrolyzerRecipeWrapper
import io.enderdev.alchemistry.compat.jei.evaporator.EvaporatorRecipeCategory
import io.enderdev.alchemistry.compat.jei.evaporator.EvaporatorRecipeWrapper
import io.enderdev.alchemistry.compat.jei.fission.FissionRecipeCategory
import io.enderdev.alchemistry.compat.jei.fission.FissionRecipeWrapper
import io.enderdev.alchemistry.compat.jei.fusion.FusionRecipeCategory
import io.enderdev.alchemistry.compat.jei.fusion.FusionRecipeWrapper
import io.enderdev.alchemistry.compat.jei.liquifier.LiquifierRecipeCategory
import io.enderdev.alchemistry.compat.jei.liquifier.LiquifierRecipeWrapper
import al132.alib.utils.extensions.toStack
import al132.alib.utils.extensions.translate
import io.enderdev.alchemistry.recipes.AtomizerRecipe
import io.enderdev.alchemistry.recipes.CombinerRecipe
import io.enderdev.alchemistry.recipes.DissolverRecipe
import io.enderdev.alchemistry.recipes.ElectrolyzerRecipe
import io.enderdev.alchemistry.recipes.EvaporatorRecipe
import io.enderdev.alchemistry.recipes.FissionRecipe
import io.enderdev.alchemistry.recipes.FusionRecipe
import io.enderdev.alchemistry.recipes.LiquifierRecipe
import io.enderdev.alchemistry.recipes.register.AtomizerRegister
import io.enderdev.alchemistry.recipes.register.CombinerRegister
import io.enderdev.alchemistry.recipes.register.DissolverRegister
import io.enderdev.alchemistry.recipes.register.ElectrolyzerRegister
import io.enderdev.alchemistry.recipes.register.EvaporatorRegister
import io.enderdev.alchemistry.recipes.register.FissionRegister
import io.enderdev.alchemistry.recipes.register.FusionRegister
import io.enderdev.alchemistry.recipes.register.LiquifierRegister
import mezz.jei.api.*
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry


@JEIPlugin
class AlchemistryPlugin : IModPlugin {

    companion object {
        lateinit var jeiHelpers: IJeiHelpers
        lateinit var recipeRegistry: IRecipeRegistry
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        recipeRegistry = jeiRuntime.recipeRegistry
    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {
        registry.jeiHelpers.guiHelper.let { guiHelper ->
            registry.addRecipeCategories(
                DissolverRecipeCategory(guiHelper),
                CombinerRecipeCategory(guiHelper),
                ElectrolyzerRecipeCategory(guiHelper),
                EvaporatorRecipeCategory(guiHelper),
                AtomizerRecipeCategory(guiHelper),
                LiquifierRecipeCategory(guiHelper),
                FissionRecipeCategory(guiHelper),
                FusionRecipeCategory(guiHelper)
            )
        }
    }

    override fun register(registry: IModRegistry) {
        jeiHelpers = registry.jeiHelpers

        registry.handleRecipes(
            DissolverRecipe::class.java,
                { recipe -> DissolverRecipeWrapper(recipe) },
            AlchemistryRecipeUID.DISSOLVER
        )
        registry.handleRecipes(
            ElectrolyzerRecipe::class.java,
                { recipe -> ElectrolyzerRecipeWrapper(recipe) },
            AlchemistryRecipeUID.ELECTROLYZER
        )
        registry.handleRecipes(
            CombinerRecipe::class.java,
                { recipe -> CombinerRecipeWrapper(recipe) },
            AlchemistryRecipeUID.COMBINER
        )
        registry.handleRecipes(
            EvaporatorRecipe::class.java,
                { recipe -> EvaporatorRecipeWrapper(recipe) },
            AlchemistryRecipeUID.EVAPORATOR
        )
        registry.handleRecipes(
            AtomizerRecipe::class.java,
                { recipe -> AtomizerRecipeWrapper(recipe) },
            AlchemistryRecipeUID.ATOMIZER
        )
        registry.handleRecipes(
            LiquifierRecipe::class.java,
                { recipe -> LiquifierRecipeWrapper(recipe) },
            AlchemistryRecipeUID.LIQUIFIER
        )
        registry.handleRecipes(
            FissionRecipe::class.java,
                { recipe -> FissionRecipeWrapper(recipe) },
            AlchemistryRecipeUID.FISSION
        )
        registry.handleRecipes(
            FusionRecipe::class.java,
                { recipe -> FusionRecipeWrapper(recipe) },
            AlchemistryRecipeUID.FUSION
        )

        registry.addRecipes(
            DissolverRegister.Companion.INSTANCE.recipes.map { DissolverRecipeWrapper(it) },
            AlchemistryRecipeUID.DISSOLVER
        )
        registry.addRecipes(
            CombinerRegister.Companion.INSTANCE.recipes.map { CombinerRecipeWrapper(it) },
            AlchemistryRecipeUID.COMBINER
        )
        registry.addRecipes(
            ElectrolyzerRegister.Companion.INSTANCE.recipes.map { ElectrolyzerRecipeWrapper(it) },
            AlchemistryRecipeUID.ELECTROLYZER
        )
        registry.addRecipes(
            EvaporatorRegister.Companion.INSTANCE.recipes.map { EvaporatorRecipeWrapper(it) },
            AlchemistryRecipeUID.EVAPORATOR
        )
        registry.addRecipes(
            AtomizerRegister.Companion.INSTANCE.recipes.map { AtomizerRecipeWrapper(it) },
            AlchemistryRecipeUID.ATOMIZER
        )
        registry.addRecipes(
            LiquifierRegister.Companion.INSTANCE.recipes.map { LiquifierRecipeWrapper(it) },
            AlchemistryRecipeUID.LIQUIFIER
        )
        registry.addRecipes(
            FissionRegister.Companion.INSTANCE.recipes.map { FissionRecipeWrapper(it) },
            AlchemistryRecipeUID.FISSION
        )
        registry.addRecipes(
            FusionRegister.Companion.INSTANCE.recipes.map { FusionRecipeWrapper(it) },
            AlchemistryRecipeUID.FUSION
        )

        registry.addRecipeClickArea(GuiChemicalDissolver::class.java, 63, 86, 32, 44, AlchemistryRecipeUID.DISSOLVER)
        registry.addRecipeClickArea(GuiChemicalCombiner::class.java, 102, 90, 27, 36, AlchemistryRecipeUID.COMBINER)
        registry.addRecipeClickArea(GuiElectrolyzer::class.java, 70, 99, 36, 36, AlchemistryRecipeUID.ELECTROLYZER)
        registry.addRecipeClickArea(GuiEvaporator::class.java, 70, 118, 36, 16, AlchemistryRecipeUID.EVAPORATOR)
        registry.addRecipeClickArea(GuiAtomizer::class.java, 70, 118, 36, 16, AlchemistryRecipeUID.ATOMIZER)
        registry.addRecipeClickArea(GuiLiquifier::class.java, 70, 118, 36, 16, AlchemistryRecipeUID.LIQUIFIER)
        registry.addRecipeClickArea(GuiFissionController::class.java, 70, 75, 36, 16, AlchemistryRecipeUID.FISSION)
        registry.addRecipeClickArea(GuiFusionController::class.java, 88, 75, 36, 16, AlchemistryRecipeUID.FUSION)

        registry.addRecipeCatalyst(ModBlocks.chemical_dissolver.toStack(), AlchemistryRecipeUID.DISSOLVER)
        registry.addRecipeCatalyst(ModBlocks.chemical_combiner.toStack(), AlchemistryRecipeUID.COMBINER)
        registry.addRecipeCatalyst(ModBlocks.electrolyzer.toStack(), AlchemistryRecipeUID.ELECTROLYZER)
        registry.addRecipeCatalyst(ModBlocks.evaporator.toStack(), AlchemistryRecipeUID.EVAPORATOR)
        registry.addRecipeCatalyst(ModBlocks.atomizer.toStack(), AlchemistryRecipeUID.ATOMIZER)
        registry.addRecipeCatalyst(ModBlocks.liquifier.toStack(), AlchemistryRecipeUID.LIQUIFIER)
        registry.addRecipeCatalyst(ModBlocks.fissionController.toStack(), AlchemistryRecipeUID.FISSION)
        registry.addRecipeCatalyst(ModBlocks.fusionController.toStack(), AlchemistryRecipeUID.FUSION)

        val transferRegistry: IRecipeTransferRegistry = registry.recipeTransferRegistry
        transferRegistry.addRecipeTransferHandler(CombinerTransferHandler(), AlchemistryRecipeUID.COMBINER)
        transferRegistry.addRecipeTransferHandler(
            ContainerChemicalDissolver::class.java,
            AlchemistryRecipeUID.DISSOLVER, 0, 1, 11, 36)
        transferRegistry.addRecipeTransferHandler(
            ContainerLiquifier::class.java,
            AlchemistryRecipeUID.LIQUIFIER, 0, 1, 1, 36)
        transferRegistry.addRecipeTransferHandler(
            ContainerElectrolyzer::class.java,
            AlchemistryRecipeUID.ELECTROLYZER, 0, 1, 1, 36)
        transferRegistry.addRecipeTransferHandler(
            ContainerFissionController::class.java,
            AlchemistryRecipeUID.FISSION, 0, 1, 3, 36)
        transferRegistry.addRecipeTransferHandler(
            ContainerFusionController::class.java,
            AlchemistryRecipeUID.FUSION, 0, 1, 3, 36)
    }
}

object AlchemistryRecipeUID {
    val COMBINER = Reference.MODID + ".combiner"
    val DISSOLVER = Reference.MODID + ".dissolver"
    val ELECTROLYZER = Reference.MODID + ".electrolyzer"
    val EVAPORATOR = Reference.MODID + ".evaporator"
    val ATOMIZER = Reference.MODID + ".atomizer"
    val LIQUIFIER = Reference.MODID + ".liquifier"
    val FISSION = Reference.MODID + ".fission"
    val FUSION = Reference.MODID + ".fusion"
}

abstract class AlchemistryRecipeWrapper<out R>(val recipe: R) : IRecipeWrapper

abstract class AlchemistryRecipeCategory<T : IRecipeWrapper>(private val background: IDrawable, unlocalizedName: String) :
        IRecipeCategory<T> {

    val localizedName: String = unlocalizedName.translate()

    override fun getBackground(): IDrawable = background

    override fun getModName() = Reference.MODID
}