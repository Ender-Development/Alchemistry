package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.tiles.TileChemicalDissolver
import io.enderdev.alchemistry.utils.extensions.get
import io.enderdev.alchemistry.utils.extensions.translate
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class TopChemicalDissolver : TopTileHandler<TileChemicalDissolver>("chemical_dissolver", TileChemicalDissolver::class.java) {
	override fun addInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData, te: TileChemicalDissolver) {
		val input = te.input[0]
		if(!input.isEmpty)
			info.item(input).text("$translationKey.dissolving".translate(input.displayName))
	}
}
