package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.tiles.TileChemicalCombiner
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import org.ender_development.catalyx.utils.extensions.translate

class TopChemicalCombiner : TopTileHandler<TileChemicalCombiner>("chemical_combiner", TileChemicalCombiner::class.java) {
	override fun addInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData, te: TileChemicalCombiner) {
		info.text("$translationKey.${if(te.recipeIsLocked) "" else "un"}locked".translate())
		te.currentRecipe?.output?.apply {
			info.item(this).text("$generic.crafting".translate(displayName))
		}
	}
}
