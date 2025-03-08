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
    private val clientsideMixinConfig: Map<String, () -> Boolean> = mapOf(
        "mixins.alchemistry.minecraft.json" to { true }
    )
    private val commonMixinConfig: Map<String, () -> Boolean> = mapOf()


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
        val config: MutableList<String> = mutableListOf()
        if (isClient) {
            config.addAll(clientsideMixinConfig.keys)
        } else {
            config.addAll(serversideMixinConfig.keys)
        }
        config.addAll(commonMixinConfig.keys)
        return config
    }

    override fun shouldMixinConfigQueue(mixinConfig: String?): Boolean {
        val loadClient = isClient && clientsideMixinConfig[mixinConfig]?.invoke() == true
        val loadCommon = commonMixinConfig[mixinConfig]?.invoke() == true
        return if (loadClient) true else loadCommon
    }

}