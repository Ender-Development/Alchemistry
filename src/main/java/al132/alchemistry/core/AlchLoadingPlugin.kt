package al132.alchemistry.core

import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.fml.relauncher.FMLLaunchHandler
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import zone.rong.mixinbooter.IEarlyMixinLoader

@IFMLLoadingPlugin.Name("AlchemistryCore")
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE)
class AlchLoadingPlugin : IFMLLoadingPlugin, IEarlyMixinLoader {

    val isClient: Boolean = FMLLaunchHandler.side().isClient

    private val serversideMixinConfig: Map<String, () -> Boolean> = mapOf()
    private val clientsideMixinConfig: Map<String, () -> Boolean> = mapOf()
    private val commonMixinConfig: Map<String, () -> Boolean> = mapOf(
        "mixins.alchemistry.core.json" to { true }
    )


    override fun getASMTransformerClass(): Array<out String?>? {
        return emptyArray()
    }

    override fun getModContainerClass(): String? {
        return null
    }

    override fun getSetupClass(): String? {
        return null
    }

    override fun injectData(data: Map<String?, Any?>?) {
    }

    override fun getAccessTransformerClass(): String? {
        return null
    }

    override fun getMixinConfigs(): List<String?>? {
        val configs: List<String> = mutableListOf(commonMixinConfig.keys.toString())
        if (isClient) {
            configs.plus(clientsideMixinConfig.keys.toString())
        } else {
            configs.plus(serversideMixinConfig.keys.toString())
        }
        return configs
    }

    override fun shouldMixinConfigQueue(mixinConfig: String?): Boolean {
        val sidedSupplier: (() -> Boolean)? = if (isClient) {
            clientsideMixinConfig[mixinConfig]
        } else {
            serversideMixinConfig[mixinConfig]
        }
        val commonSupplier: (() -> Boolean)? = commonMixinConfig[mixinConfig]
        return (sidedSupplier?.invoke() ?: commonSupplier?.invoke()) == true
    }

}