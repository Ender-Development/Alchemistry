package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import io.enderdev.alchemistry.utils.extensions.translate
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class TopChemicalCombiner : IProbeInfoProvider {
	override fun getID() = "${Tags.MOD_ID}.chemical_combiner"

	override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
		if(!player.isSneaking)
			return

		val te = world.getTileEntity(data.pos)
		if(te !is TileChemicalCombiner)
			return

		probeInfo.text("top.${Tags.MOD_ID}.chemical_combiner.${if(te.recipeIsLocked) "" else "un"}locked".translate())
		te.currentRecipe?.output?.apply { probeInfo.item(this) }
	}
}
