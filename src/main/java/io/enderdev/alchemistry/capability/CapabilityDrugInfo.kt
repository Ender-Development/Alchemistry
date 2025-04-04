package io.enderdev.alchemistry.capability

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject

object CapabilityDrugInfo {
	@CapabilityInject(AlchemistryDrugInfo::class)
	lateinit var DRUG_INFO: Capability<AlchemistryDrugInfo>

	fun getPlayerDrugInfo(player: EntityPlayer) =
		player.getCapability(DRUG_INFO, null)
}
