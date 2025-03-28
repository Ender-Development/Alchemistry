package io.enderdev.alchemistry.blocks

import io.enderdev.alchemistry.BaseBlock
import io.enderdev.alchemistry.blocks.machine.*
import io.enderdev.alchemistry.client.gui.GuiHandler
import io.enderdev.alchemistry.tiles.*
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModBlocks {
    val blocks = ArrayList<BaseBlock>()

    val electrolyzer =
        ElectrolyzerBlock("electrolyzer", TileElectrolyzer::class.java, GuiHandler.Companion.ELECTROLYZER_ID)
    val chemical_dissolver = ChemicalDissolverBlock(
        "chemical_dissolver",
        TileChemicalDissolver::class.java,
        GuiHandler.Companion.CHEMICAL_DISSOLVER_ID
    )
    val chemical_combiner =
        ChemicalCombinerBlock(
            "chemical_combiner",
            TileChemicalCombiner::class.java,
            GuiHandler.Companion.CHEMICAL_COMBINER_ID
        )
    val evaporator = EvaporatorBlock("evaporator", TileEvaporator::class.java, GuiHandler.Companion.EVAPORATOR_ID)
    val atomizer = AtomizerBlock("atomizer", TileAtomizer::class.java, GuiHandler.Companion.ATOMIZER_ID)
    val liquifier = LiquifierBlock("liquifier", TileLiquifier::class.java, GuiHandler.Companion.LIQUIFIER_ID)

    val fissionCasing: BaseBlock = BaseBlock("fission_casing")
    val fissionGlass: BaseBlock = GlassBlock("fission_glass")
    val fissionCore: BaseBlock = CoreBlock("fission_core")
    val fissionController = FissionControllerBlock(
        "fission_controller",
        TileFissionController::class.java,
        GuiHandler.Companion.FISSION_CONTROLLER_ID
    )

    val fusionCasing: BaseBlock = BaseBlock("fusion_casing")
    val fusionGlass: BaseBlock = GlassBlock("fusion_glass")
    val fusionCore: BaseBlock = CoreBlock("fusion_core")
    val fusionController =
        FusionControllerBlock(
            "fusion_controller",
            TileFusionController::class.java,
            GuiHandler.Companion.FUSION_CONTROLLER_ID
        )

    val neonLight = LightBlock("neon_light")//red-orange
    val heliumLight = LightBlock("helium_light") //red
    val argonLight = LightBlock("argon_light") //purple-blue
    val kryptonLight = LightBlock("krypton_light") //light yellow or green
    val xenonLight = LightBlock("xenon_light") //gray-blue
    val radonLight = LightBlock("radon_light") //green

    val wetSand = WetSandBlock()

    fun registerBlocks(event: RegistryEvent.Register<Block>) = blocks.forEach { it.registerBlock(event) }

    fun registerItemBlocks(event: RegistryEvent.Register<Item>) = blocks.forEach { it.registerItemBlock(event) }

    @SideOnly(Side.CLIENT)
    fun registerModels() = blocks.forEach { it.registerModel() }
}