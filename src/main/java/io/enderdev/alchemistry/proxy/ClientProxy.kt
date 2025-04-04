package io.enderdev.alchemistry.proxy

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.items.ModItems
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy() {

	override fun preInit(e: FMLPreInitializationEvent) {
		super.preInit(e)
		OBJLoader.INSTANCE.addDomain(Tags.MOD_ID)
	}

	override fun postInit(e: FMLPostInitializationEvent) {
		super.postInit(e)
		ModItems.initColors()
		MinecraftForge.EVENT_BUS.register(ClientEventHandler())
	}
}
