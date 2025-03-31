package io.enderdev.alchemistry.capability;

import net.minecraft.nbt.NBTTagCompound

//Peeking off https://github.com/McJtyMods/TheOneProbe/blob/1.12/src/main/java/mcjty/theoneprobe/playerdata/PlayerGotNote.java
class AlchemistryDrugInfo {
	var psilocybinTicks = 0
	var cumulativeFOVModifier = 1f

	fun saveNBTData(compound: NBTTagCompound) {
		compound.setInteger("psilocybinTicks", psilocybinTicks)
		compound.setFloat("cumulativeFOVModifier", cumulativeFOVModifier)
	}

	fun loadNBTData(compound: NBTTagCompound) {
		psilocybinTicks = compound.getInteger("psilocybinTicks")
		cumulativeFOVModifier = compound.getFloat("cumulativeFOVModifier")
	}
}