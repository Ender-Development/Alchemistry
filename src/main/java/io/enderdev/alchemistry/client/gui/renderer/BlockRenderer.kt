package io.enderdev.alchemistry.client.gui.renderer

import io.enderdev.alchemistry.client.gui.GuiReactorModifiers
import io.enderdev.alchemistry.tiles.AbstractReactorController.BlockMeta
import io.enderdev.alchemistry.utils.extensions.toStack

class BlockRenderer(block: BlockMeta, val self: GuiReactorModifiers) : IRenderer {
	override val textureX = 193
	val stack = block.block.toStack(meta = block.meta)
	override fun render(x: Int, y: Int, offY: Int) =
		self.renderItemAndEffectIntoGUI(stack, x + 8, y + offY)

	override fun renderTooltip(mouseX: Int, mouseY: Int) =
		self.renderToolTip(stack, mouseX, mouseY)
}
