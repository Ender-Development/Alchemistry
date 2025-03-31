package io.enderdev.alchemistry.capability;

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.INBTSerializable

//Peeking off https://github.com/McJtyMods/TheOneProbe/blob/1.12/src/main/java/mcjty/theoneprobe/playerdata/PropertiesDispatcher.java
class AlchemistryDrugDispatcher : ICapabilityProvider, INBTSerializable<NBTTagCompound> {
	private var drugInfo = AlchemistryDrugInfo()

	override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
		capability == CapabilityDrugInfo.DRUG_INFO;

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any?> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? =
		if(hasCapability(capability, facing)) drugInfo as T else null

	override fun serializeNBT(): NBTTagCompound? {
		val nbt = NBTTagCompound()
		drugInfo.saveNBTData(nbt)
		return nbt
	}

	override fun deserializeNBT(nbt: NBTTagCompound?) {
		nbt?.let { drugInfo.loadNBTData(nbt) }
	}
}