package al132.alchemistry.compat.jei

import al132.alchemistry.Reference
import al132.alchemistry.blocks.ModBlocks
import al132.alchemistry.client.container.ContainerChemicalDissolver
import al132.alchemistry.client.container.ContainerElectrolyzer
import al132.alchemistry.client.container.ContainerFissionController
import al132.alchemistry.client.container.ContainerFusionController
import al132.alchemistry.client.container.ContainerLiquifier
import al132.alchemistry.client.gui.GuiAtomizer
import al132.alchemistry.client.gui.GuiChemicalCombiner
import al132.alchemistry.client.gui.GuiChemicalDissolver
import al132.alchemistry.client.gui.GuiElectrolyzer
import al132.alchemistry.client.gui.GuiEvaporator
import al132.alchemistry.client.gui.GuiFissionController
import al132.alchemistry.client.gui.GuiFusionController
import al132.alchemistry.client.gui.GuiLiquifier
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.ATOMIZER
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.COMBINER
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.DISSOLVER
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.ELECTROLYZER
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.EVAPORATOR
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.FISSION
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.FUSION
import al132.alchemistry.compat.jei.AlchemistryRecipeUID.LIQUIFIER
import al132.alchemistry.compat.jei.atomizer.AtomizerRecipeCategory
import al132.alchemistry.compat.jei.atomizer.AtomizerRecipeWrapper
import al132.alchemistry.compat.jei.combiner.CombinerRecipeCategory
import al132.alchemistry.compat.jei.combiner.CombinerRecipeWrapper
import al132.alchemistry.compat.jei.combiner.CombinerTransferHandler
import al132.alchemistry.compat.jei.dissolver.DissolverRecipeCategory
import al132.alchemistry.compat.jei.dissolver.DissolverRecipeWrapper
import al132.alchemistry.compat.jei.electrolyzer.ElectrolyzerRecipeCategory
import al132.alchemistry.compat.jei.electrolyzer.ElectrolyzerRecipeWrapper
import al132.alchemistry.compat.jei.evaporator.EvaporatorRecipeCategory
import al132.alchemistry.compat.jei.evaporator.EvaporatorRecipeWrapper
import al132.alchemistry.compat.jei.fission.FissionRecipeCategory
import al132.alchemistry.compat.jei.fission.FissionRecipeWrapper
import al132.alchemistry.compat.jei.fusion.FusionRecipeCategory
import al132.alchemistry.compat.jei.fusion.FusionRecipeWrapper
import al132.alchemistry.compat.jei.liquifier.LiquifierRecipeCategory
import al132.alchemistry.compat.jei.liquifier.LiquifierRecipeWrapper
import al132.alchemistry.recipes.*
import al132.alchemistry.recipes.register.*
import al132.alib.utils.extensions.toStack
import al132.alib.utils.extensions.translate
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

        registry.handleRecipes(DissolverRecipe::class.java,
                { recipe -> DissolverRecipeWrapper(recipe) },
                DISSOLVER)
        registry.handleRecipes(ElectrolyzerRecipe::class.java,
                { recipe -> ElectrolyzerRecipeWrapper(recipe) },
                ELECTROLYZER)
        registry.handleRecipes(CombinerRecipe::class.java,
                { recipe -> CombinerRecipeWrapper(recipe) },
                COMBINER)
        registry.handleRecipes(EvaporatorRecipe::class.java,
                { recipe -> EvaporatorRecipeWrapper(recipe) },
                EVAPORATOR)
        registry.handleRecipes(AtomizerRecipe::class.java,
                { recipe -> AtomizerRecipeWrapper(recipe) },
                ATOMIZER)
        registry.handleRecipes(LiquifierRecipe::class.java,
                { recipe -> LiquifierRecipeWrapper(recipe) },
                LIQUIFIER)
        registry.handleRecipes(FissionRecipe::class.java,
                { recipe -> FissionRecipeWrapper(recipe) },
                FISSION)
        registry.handleRecipes(FusionRecipe::class.java,
                { recipe -> FusionRecipeWrapper(recipe) },
                FUSION)

        registry.addRecipes(DissolverRegister.INSTANCE.recipes.map { DissolverRecipeWrapper(it) }, DISSOLVER)
        registry.addRecipes(CombinerRegister.INSTANCE.recipes.map { CombinerRecipeWrapper(it) }, COMBINER)
        registry.addRecipes(ElectrolyzerRegister.INSTANCE.recipes.map { ElectrolyzerRecipeWrapper(it) }, ELECTROLYZER)
        registry.addRecipes(EvaporatorRegister.INSTANCE.recipes.map { EvaporatorRecipeWrapper(it) }, EVAPORATOR)
        registry.addRecipes(AtomizerRegister.INSTANCE.recipes.map { AtomizerRecipeWrapper(it) }, ATOMIZER)
        registry.addRecipes(LiquifierRegister.INSTANCE.recipes.map { LiquifierRecipeWrapper(it) }, LIQUIFIER)
        registry.addRecipes(FissionRegister.INSTANCE.recipes.map { FissionRecipeWrapper(it) }, FISSION)
        registry.addRecipes(FusionRegister.INSTANCE.recipes.map { FusionRecipeWrapper(it) }, FUSION)

        registry.addRecipeClickArea(GuiChemicalDissolver::class.java, 63, 86, 32, 44, DISSOLVER)
        registry.addRecipeClickArea(GuiChemicalCombiner::class.java, 102, 90, 27, 36, COMBINER)
        registry.addRecipeClickArea(GuiElectrolyzer::class.java, 70, 99, 36, 36, ELECTROLYZER)
        registry.addRecipeClickArea(GuiEvaporator::class.java, 70, 118, 36, 16, EVAPORATOR)
        registry.addRecipeClickArea(GuiAtomizer::class.java, 70, 118, 36, 16, ATOMIZER)
        registry.addRecipeClickArea(GuiLiquifier::class.java, 70, 118, 36, 16, LIQUIFIER)
        registry.addRecipeClickArea(GuiFissionController::class.java, 70, 75, 36, 16, FISSION)
        registry.addRecipeClickArea(GuiFusionController::class.java, 88, 75, 36, 16, FUSION)

        registry.addRecipeCatalyst(ModBlocks.chemical_dissolver.toStack(), DISSOLVER)
        registry.addRecipeCatalyst(ModBlocks.chemical_combiner.toStack(), COMBINER)
        registry.addRecipeCatalyst(ModBlocks.electrolyzer.toStack(), ELECTROLYZER)
        registry.addRecipeCatalyst(ModBlocks.evaporator.toStack(), EVAPORATOR)
        registry.addRecipeCatalyst(ModBlocks.atomizer.toStack(), ATOMIZER)
        registry.addRecipeCatalyst(ModBlocks.liquifier.toStack(), LIQUIFIER)
        registry.addRecipeCatalyst(ModBlocks.fissionController.toStack(), FISSION)
        registry.addRecipeCatalyst(ModBlocks.fusionController.toStack(), FUSION)

        val transferRegistry: IRecipeTransferRegistry = registry.recipeTransferRegistry
        transferRegistry.addRecipeTransferHandler(CombinerTransferHandler(), COMBINER)
        transferRegistry.addRecipeTransferHandler(ContainerChemicalDissolver::class.java, DISSOLVER, 0, 1, 11, 36)
        transferRegistry.addRecipeTransferHandler(ContainerLiquifier::class.java, LIQUIFIER, 0, 1, 1, 36)
        transferRegistry.addRecipeTransferHandler(ContainerElectrolyzer::class.java, ELECTROLYZER, 0, 1, 1, 36)
        transferRegistry.addRecipeTransferHandler(ContainerFissionController::class.java, FISSION, 0, 1, 3, 36)
        transferRegistry.addRecipeTransferHandler(ContainerFusionController::class.java, FUSION, 0, 1, 3, 36)
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