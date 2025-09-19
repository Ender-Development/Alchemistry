package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.tiles.TileEvaporator
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.ender_development.catalyx.utils.extensions.translate

class TopEvaporator : TopTileHandler<TileEvaporator>("evaporator", TileEvaporator::class.java) {
	override fun addInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData, te: TileEvaporator) {
		te.currentRecipe?.apply {
			info.item(output).text("$translationKey.evaporating".translate(input.localizedName, output.displayName))
		}
	}
}
