package io.enderdev.alchemistry.compat.jei


import io.enderdev.alchemistry.Reference
import io.enderdev.alchemistry.blocks.ModBlocks
import io.enderdev.alchemistry.client.container.*
import io.enderdev.alchemistry.client.gui.*
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
import io.enderdev.alchemistry.recipes.*
import io.enderdev.alchemistry.recipes.register.*
import io.enderdev.alchemistry.utils.extensions.toStack
import io.enderdev.alchemistry.utils.extensions.translate
import mezz.jei.api.*
import mezz.jei.api.gui.IDrawableStatic
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry
import net.minecraft.util.ResourceLocation


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
    const val COMBINER = Reference.MODID + ".combiner"
    const val DISSOLVER = Reference.MODID + ".dissolver"
    const val ELECTROLYZER = Reference.MODID + ".electrolyzer"
    const val EVAPORATOR = Reference.MODID + ".evaporator"
    const val ATOMIZER = Reference.MODID + ".atomizer"
    const val LIQUIFIER = Reference.MODID + ".liquifier"
    const val FISSION = Reference.MODID + ".fission"
    const val FUSION = Reference.MODID + ".fusion"
}

abstract class AlchemistryRecipeWrapper<out R>(val recipe: R) : IRecipeWrapper

abstract class AlchemistryRecipeCategory<T : IRecipeWrapper>(val guiHelper: IGuiHelper, guiName: String) : IRecipeCategory<T> {
    val localizedName: String = "jei.$guiName.name".translate()

    open val guiTexture = ResourceLocation(Reference.MODID, "textures/gui/container/${guiName}_gui_redox.png")
    abstract val u: Int
    abstract val v: Int
    abstract val width: Int
    abstract val height: Int
    private var backgroundDrawable: IDrawableStatic? = null

    override fun getTitle() = localizedName

    override fun getModName() = Reference.MODID

    override fun getBackground(): IDrawableStatic {
        backgroundDrawable = backgroundDrawable ?: guiHelper.createDrawable(guiTexture, u, v, width, height)
        return backgroundDrawable!!
    }
}