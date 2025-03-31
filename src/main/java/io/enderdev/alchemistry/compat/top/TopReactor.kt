package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.Tags
import io.enderdev.alchemistry.tiles.AbstractReactorController
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class TopReactor() : IProbeInfoProvider {
	override fun getID(): String? {
		return "${Tags.MOD_ID}.reactor"
	}

	override fun addProbeInfo(mode: ProbeMode, probeInfo : IProbeInfo, player : EntityPlayer, world: World, blockState: IBlockState, data: IProbeHitData) {
		if (player.isSneaking && world.getTileEntity(data.pos) is AbstractReactorController<*>) {
			val tile = world.getTileEntity(data.pos) as AbstractReactorController<*>
			if (tile.isMultiblockValid) {
				val mod = tile.currentModifier
				probeInfo.text("Productivity: %.2f%%".format(mod.productivity * 100))
				probeInfo.text("Speed: %.2f%%".format(mod.speed * 100))
				probeInfo.text("Energy: %.2f%%".format(mod.energy * 100))
			} else {
				probeInfo.text("Invalid Multiblock")
			}
		}
	}
}
