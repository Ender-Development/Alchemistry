package al132.alchemistry.items

import al132.alchemistry.client.ElementItemStackRenderer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.FMLLaunchHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemTEISR(name: String) : ItemMetaBase(name) {
    init {
        if (FMLLaunchHandler.side() == Side.CLIENT) setTEISR()
    }

    @SideOnly(Side.CLIENT)
    private fun setTEISR() {
        this.tileEntityItemStackRenderer = ElementItemStackRenderer().get()
    }
}