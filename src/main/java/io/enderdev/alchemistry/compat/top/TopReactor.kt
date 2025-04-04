package io.enderdev.alchemistry.compat.top

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.chemistry.ElementRegistry
import io.enderdev.alchemistry.tiles.AbstractReactorController
import io.enderdev.alchemistry.tiles.TileFissionController
import io.enderdev.alchemistry.tiles.TileFusionController
import io.enderdev.alchemistry.utils.extensions.translate
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

class TopReactor() : TopTileHandler<AbstractReactorController<*>>("reactor", AbstractReactorController::class.java) {
	override fun addInfo(mode: ProbeMode, info: IProbeInfo, player: EntityPlayer, world: World, state: IBlockState, data: IProbeHitData, te: AbstractReactorController<*>) {
		if(te.isMultiblockValid) {
			val (productivity, processingTime, energy) = te.currentMultiplier
			info.text("tile.reactor.output_multiplier".translate("${Alchemistry.DECIMAL_FORMAT.format(productivity)}x"))
				.text("tile.reactor.processing_time".translate("${Alchemistry.DECIMAL_FORMAT.format(processingTime)}x"))
				.text("tile.reactor.energy_consumption".translate("${Alchemistry.DECIMAL_FORMAT.format(energy)}x"))

			val stack = { meta: Int -> ElementRegistry[meta]!!.toItemStack(1) }
			if(te is TileFissionController)
				te.currentRecipe?.apply {
					val section = info.horizontal().item(stack(inputMeta)).text(" -> ")
					val out1 = stack(output1Meta)
					if(output2Meta == 0)
						section.item(out1.apply { count = 2 })
					else
						section.item(out1).text(" + ").item(stack(output2Meta))
				}
			else if(te is TileFusionController)
				te.currentRecipe?.apply {
					info.horizontal().item(stack(inputMeta1)).text(" + ").item(stack(inputMeta2)).text(" -> ").item(stack(outputMeta))
				}
		} else
			info.text("$translationKey.invalid".translate())
	}
}
