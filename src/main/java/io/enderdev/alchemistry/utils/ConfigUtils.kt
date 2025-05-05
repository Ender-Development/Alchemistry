package io.enderdev.alchemistry.utils

import io.enderdev.alchemistry.Alchemistry
import io.enderdev.alchemistry.client.gui.misc.GuiModifiers
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidRegistry
import java.util.*

object ConfigUtils {
	fun parseBlock(blockStr: String): BlockMeta? {
		val tokenizer = StringTokenizer(blockStr, ":")

		val mod: String
		val name: String
		val meta: Int?

		try {
			mod = tokenizer.nextToken()
			name = tokenizer.nextToken()
			meta = if(tokenizer.hasMoreTokens()) tokenizer.nextToken().toInt() else null
		} catch(_: NoSuchElementException) {
			Alchemistry.logger.error("Invalid block specification, expected at least 2 sections: '$blockStr'")
			return null
		} catch(_: NumberFormatException) {
			Alchemistry.logger.error("Invalid block specification, expected meta (column 3) to be a number or empty: '$blockStr'")
			return null
		}

		val location = ResourceLocation(mod, name)

		if(!Block.REGISTRY.containsKey(location)) {
			Alchemistry.logger.error("Invalid block, doesn't exist: '$blockStr'")
			return null
		}

		return BlockMeta(Block.REGISTRY.getObject(location), meta)
	}
}

data class BlockMeta(val block: Block, val meta: Int?) {
	fun matches(state: IBlockState) = state.block == block && (meta == null || block.getMetaFromState(state) == meta)

	override fun equals(other: Any?): Boolean {
		return when(other) {
			is IBlockState -> matches(other)
			is BlockMeta -> block == other.block && meta == other.meta
			else -> false
		}
	}

	fun getGUIRenderer(self: GuiScreen): GuiModifiers.IRenderer {
		val fluid = FluidRegistry.lookupFluidForBlock(block)
		return if(fluid != null)
			GuiModifiers.FluidRenderer(fluid, self)
		else
			GuiModifiers.BlockRenderer(this, self)
	}

	override fun hashCode() = block.hashCode() + (meta ?: 10023) * 37
}
