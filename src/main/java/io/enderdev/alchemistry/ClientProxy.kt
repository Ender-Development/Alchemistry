package io.enderdev.alchemistry


import io.enderdev.alchemistry.items.ModItems
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly


class ClientProxy : CommonProxy() {

    @SideOnly(Side.CLIENT)
    override fun preInit(e: FMLPreInitializationEvent) {
        super.preInit(e)
        OBJLoader.INSTANCE.addDomain(Tags.MOD_ID)
    }

    override fun postInit(e: FMLPostInitializationEvent) {
        super.postInit(e)
        ModItems.initColors()
        MinecraftForge.EVENT_BUS.register(ClientEventHandler())
    }

    companion object {
        //var highAF: Int = 0
        //var cumulativeFovModifier = 1.0f
    }
}