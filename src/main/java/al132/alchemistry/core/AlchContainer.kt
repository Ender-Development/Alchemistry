package al132.alchemistry.core

import al132.alchemistry.Reference
import com.google.common.eventbus.EventBus
import net.minecraftforge.fml.common.DummyModContainer
import net.minecraftforge.fml.common.LoadController
import net.minecraftforge.fml.common.ModMetadata

class AlchContainer: DummyModContainer(ModMetadata()) {
    init {
        val meta = this.metadata
        meta.modId = Reference.MODID + "-core"
        meta.name = Reference.MODNAME + " Core"
        meta.version = Reference.VERSION
        meta.authorList.add("EnderDev")
        meta.description = "A mod that adds chemistry to Minecraft"
    }

    override fun registerBus(bus: EventBus, controller: LoadController): Boolean {
        bus.register(this)
        return true
    }
}