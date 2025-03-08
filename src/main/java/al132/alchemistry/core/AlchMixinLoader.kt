package al132.alchemistry.core

import net.minecraftforge.fml.common.Loader
import zone.rong.mixinbooter.ILateMixinLoader

class AlchMixinLoader: ILateMixinLoader {
    private val mixinConfigs: Map<String, () -> Boolean> = mapOf(
        "mixins.alchemistry.jei.json" to { Loader.isModLoaded("jei") }
    )

    override fun getMixinConfigs(): List<String?>? {
        return mixinConfigs.keys.toList()
    }

    override fun shouldMixinConfigQueue(mixinConfig: String?): Boolean {
        return mixinConfigs[mixinConfig]?.invoke() == true
    }
}