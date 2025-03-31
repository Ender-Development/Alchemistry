package io.enderdev.alchemistry

import crafttweaker.CraftTweakerAPI
import io.enderdev.alchemistry.capability.AlchemistryDrugInfo
import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.client.gui.GuiHandler
import io.enderdev.alchemistry.network.PacketHandler
import io.enderdev.alchemistry.recipes.ModRecipes
import io.enderdev.alchemistry.recipes.XMLRecipeParser
import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import java.io.File
import java.util.*

open class CommonProxy {

    companion object {
        private var stage: LoadingStage = LoadingStage.PRE_INIT

        fun getStage(): LoadingStage {
            return stage
        }
    }

    open fun preInit(e: FMLPreInitializationEvent) {
        stage = LoadingStage.PRE_INIT
        Alchemistry.logger = e.modLog
        Alchemistry.configPath = e.suggestedConfigurationFile.parent
        Alchemistry.configDir = File(e.modConfigurationDirectory, Tags.MOD_ID)
        if (!Alchemistry.configDir.exists()) Alchemistry.configDir.mkdir()
        val exampleFile = File(Alchemistry.configDir, "custom.xml")
        if (!exampleFile.exists()) {
            exampleFile.printWriter().use { out ->
                out.println("<!--Read the wiki for more info on using custom recipes https://github.com/al132mc/alchemistry/wiki -->")
                out.println("<recipes>")
                out.println("</recipes>")
            }
        }
        registerCapabilities()
        if (ElementRegistry.getAllElements().isEmpty()) {
            Alchemistry.logger.info("ElementRegistry isn't initialized yet, initializing")
            ElementRegistry.init()
        }
        if (CompoundRegistry.compounds().isEmpty()) {
            Alchemistry.logger.info("CompoundRegistry isn't initialized yet, initializing")
            CompoundRegistry.init()
        }
        PacketHandler.registerMessages(Tags.MOD_ID)

        if (Loader.isModLoaded("crafttweaker")) CraftTweakerAPI.tweaker.loadScript(false, Tags.MOD_ID)
    }

    open fun init(e: FMLInitializationEvent) {
        stage = LoadingStage.INIT
        ModRecipes.initOredict()
        Alchemistry.configDir
            .listFiles { it.extension.lowercase(Locale.getDefault()) == "xml" }
            ?.forEach { XMLRecipeParser().init(it.name) }
        NetworkRegistry.INSTANCE.registerGuiHandler(Alchemistry, GuiHandler())
        MinecraftForge.EVENT_BUS.register(EventHandler())
    }

    open fun postInit(e: FMLPostInitializationEvent) {
        stage = LoadingStage.POST_INIT
        ModRecipes.init()
    }

    private fun registerCapabilities() {
        CapabilityManager.INSTANCE.register(
            AlchemistryDrugInfo::class.java,
            object : Capability.IStorage<AlchemistryDrugInfo> {

                override fun writeNBT(
                    capability: Capability<AlchemistryDrugInfo>,
                    instance: AlchemistryDrugInfo,
                    side: EnumFacing
                ): NBTBase? {
                    throw UnsupportedOperationException()
                }

                override fun readNBT(
                    capability: Capability<AlchemistryDrugInfo>,
                    instance: AlchemistryDrugInfo,
                    side: EnumFacing,
                    nbt: NBTBase
                ) {
                    throw UnsupportedOperationException()
                }

            }) { throw UnsupportedOperationException() }
    }

    enum class LoadingStage {
        PRE_INIT, INIT, POST_INIT
    }
}