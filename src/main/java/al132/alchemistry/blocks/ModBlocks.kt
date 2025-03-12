package al132.alchemistry.blocks

import al132.alchemistry.Reference
import al132.alchemistry.blocks.machine.AtomizerBlock
import al132.alchemistry.blocks.machine.ChemicalCombinerBlock
import al132.alchemistry.blocks.machine.ChemicalDissolverBlock
import al132.alchemistry.blocks.machine.ElectrolyzerBlock
import al132.alchemistry.blocks.machine.EvaporatorBlock
import al132.alchemistry.blocks.machine.FissionControllerBlock
import al132.alchemistry.blocks.machine.FusionControllerBlock
import al132.alchemistry.blocks.machine.LiquifierBlock
import al132.alchemistry.client.gui.GuiHandler
import al132.alchemistry.tiles.*
import al132.alib.blocks.ALBlock
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModBlocks {

    val blocks = ArrayList<ALBlock>()

    val electrolyzer = ElectrolyzerBlock("electrolyzer", TileElectrolyzer::class.java, GuiHandler.ELECTROLYZER_ID)
    val chemical_dissolver = ChemicalDissolverBlock(
        "chemical_dissolver",
        TileChemicalDissolver::class.java,
        GuiHandler.CHEMICAL_DISSOLVER_ID
    )
    val chemical_combiner =
        ChemicalCombinerBlock("chemical_combiner", TileChemicalCombiner::class.java, GuiHandler.CHEMICAL_COMBINER_ID)
    val evaporator = EvaporatorBlock("evaporator", TileEvaporator::class.java, GuiHandler.EVAPORATOR_ID)
    val atomizer = AtomizerBlock("atomizer", TileAtomizer::class.java, GuiHandler.ATOMIZER_ID)
    val liquifier = LiquifierBlock("liquifier", TileLiquifier::class.java, GuiHandler.LIQUIFIER_ID)

    val fissionCasing: BaseBlock = BaseBlock("fission_casing")
    val fissionGlass: BaseBlock = GlassBlock("fission_glass")
    val fissionCore: BaseBlock = CoreBlock("fission_core")
    val fissionController = FissionControllerBlock(
        "fission_controller",
        TileFissionController::class.java,
        GuiHandler.FISSION_CONTROLLER_ID
    )

    val fusionCasing: BaseBlock = BaseBlock("fusion_casing")
    val fusionGlass: BaseBlock = GlassBlock("fusion_glass")
    val fusionCore: BaseBlock = CoreBlock("fusion_core")
    val fusionController =
        FusionControllerBlock("fusion_controller", TileFusionController::class.java, GuiHandler.FUSION_CONTROLLER_ID)

    val neonLight = LightBlock("neon_light")//red-orange
    val heliumLight = LightBlock("helium_light") //red
    val argonLight = LightBlock("argon_light") //purple-blue
    val kryptonLight = LightBlock("krypton_light") //light yellow or green
    val xenonLight = LightBlock("xenon_light") //gray-blue

    val wetSand = WetSandBlock()

    fun registerBlocks(event: RegistryEvent.Register<Block>) = blocks.forEach { it.registerBlock(event) }

    fun registerItemBlocks(event: RegistryEvent.Register<Item>) = blocks.forEach { it.registerItemBlock(event) }

    @SideOnly(Side.CLIENT)
    fun registerModels() = blocks.forEach { it.registerModel() }
}

open class BaseBlock(name: String, material: Material = Material.ROCK) : ALBlock(name, Reference.creativeTab, material) {
    init {
        ModBlocks.blocks.add(this)
    }
}