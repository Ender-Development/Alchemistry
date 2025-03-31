package io.enderdev.alchemistry.proxy

import crafttweaker.CraftTweakerAPI
import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.proxy.CommonEventHandler
import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.capability.AlchemistryDrugInfo
import io.enderdev.alchemistry.chemistry.CompoundRegistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.client.gui.GuiHandler
import io.enderdev.alchemistry.network.PacketHandler
import io.enderdev.alchemistry.recipes.ModRecipes
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

		registerCapabilities()
		if(ElementRegistry.getAllElements().isEmpty()) {
			Alchemistry.logger.info("ElementRegistry isn't initialized yet, initializing")
			ElementRegistry.init()
		}
		if(CompoundRegistry.compounds().isEmpty()) {
			Alchemistry.logger.info("CompoundRegistry isn't initialized yet, initializing")
			CompoundRegistry.init()
		}
		PacketHandler.registerMessages(Tags.MOD_ID)

		if(Loader.isModLoaded("crafttweaker")) CraftTweakerAPI.tweaker.loadScript(false, Tags.MOD_ID)
	}

	open fun init(e: FMLInitializationEvent) {
		stage = LoadingStage.INIT
		ModRecipes.initOredict()
		NetworkRegistry.INSTANCE.registerGuiHandler(Alchemistry, GuiHandler())
		MinecraftForge.EVENT_BUS.register(CommonEventHandler())
	}

	open fun postInit(e: FMLPostInitializationEvent) {
		stage = LoadingStage.POST_INIT
		ModRecipes.init()
	}

	private fun registerCapabilities() {
		CapabilityManager.INSTANCE.register(
			AlchemistryDrugInfo::class.java, object : Capability.IStorage<AlchemistryDrugInfo> {

				override fun writeNBT(capability: Capability<AlchemistryDrugInfo>, instance: AlchemistryDrugInfo, side: EnumFacing): NBTBase? {
					throw UnsupportedOperationException()
				}

				override fun readNBT(capability: Capability<AlchemistryDrugInfo>, instance: AlchemistryDrugInfo, side: EnumFacing, nbt: NBTBase) {
					throw UnsupportedOperationException()
				}
			}) { throw UnsupportedOperationException() }
	}

	enum class LoadingStage {
		PRE_INIT, INIT, POST_INIT
	}
}
