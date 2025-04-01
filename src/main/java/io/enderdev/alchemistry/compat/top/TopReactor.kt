package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.tiles.AbstractReactorController
import io.enderdev.alchemistry.utils.extensions.translate
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import kotlin.math.roundToInt

class TopReactor() : IProbeInfoProvider {
	override fun getID() = "${Tags.MOD_ID}.reactor"

	override fun addProbeInfo(mode: ProbeMode, probeInfo: IProbeInfo, player: EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
		if(player.isSneaking && world.getTileEntity(data.pos) is AbstractReactorController<*>) {
			val tile = world.getTileEntity(data.pos) as AbstractReactorController<*>
			if(tile.isMultiblockValid) {
				val (productivity, speed, energy) = tile.currentModifier
				probeInfo.text("top.${Tags.MOD_ID}.reactor.productivity".translate(((1 + productivity) * 100).roundToInt()))
				probeInfo.text("top.${Tags.MOD_ID}.reactor.speed".translate(((1 + speed) * 100).roundToInt()))
				probeInfo.text("top.${Tags.MOD_ID}.reactor.energy".translate(((1 + energy) * 100).roundToInt()))
			} else
				probeInfo.text("top.${Tags.MOD_ID}.reactor.invalid".translate())
		}
	}
}
