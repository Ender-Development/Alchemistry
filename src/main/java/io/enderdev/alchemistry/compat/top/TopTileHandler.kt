package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.Tags
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

abstract class TopTileHandler<T>(val id: String, val te: Class<T>) : IProbeInfoProvider where T : TileEntity {
	override fun getID() = "${Tags.MOD_ID}.$id"

	val translationKey = "top.${Tags.MOD_ID}.$id"
	val generic = "top.${Tags.MOD_ID}.generic"

	override fun addProbeInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData) {
		if(mode != ProbeMode.NORMAL || !player.isSneaking)
			return

		val tile = world.getTileEntity(data.pos)
		@Suppress("UNCHECKED_CAST")
		if(te.isInstance(tile))
			addInfo(mode, info, player, world, state, data, tile as T)
	}

	abstract fun addInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData, te: T)
}
