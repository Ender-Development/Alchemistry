package io.enderdev.alchemistry.mixins

import zone.rong.mixinbooter.ILateMixinLoader

class MixinLoader : ILateMixinLoader {
	private val mixinConfigs: Map<String, () -> Boolean> = mapOf()

	override fun getMixinConfigs(): List<String?>? {
		return mixinConfigs.keys.toList()
	}

	override fun shouldMixinConfigQueue(mixinConfig: String?): Boolean {
		return mixinConfigs[mixinConfig]?.invoke() == true
	}
}
