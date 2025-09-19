package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.items.ItemCompound
import io.enderdev.alchemistry.items.ItemElement
import io.enderdev.alchemistry.tiles.TileLiquifier
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.ender_development.catalyx.utils.extensions.translate

class TopLiquifier : TopTileHandler<TileLiquifier>("liquifier", TileLiquifier::class.java) {
	override fun addInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData, te: TileLiquifier) {
		te.currentRecipe?.apply {
			var inputName = input.displayName
			if(input.item is ItemCompound || input.item is ItemElement)
				inputName += " ${"$generic.molecules".translate()}"
			info.item(input).text("$translationKey.liquifying".translate(inputName, output.localizedName))
		}
	}
}
