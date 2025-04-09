package io.enderdev.alchemistry.client.gui.renderer

interface IRenderer {
	val textureX: Int
	fun render(x: Int, y: Int, offY: Int)
	fun renderTooltip(mouseX: Int, mouseY: Int)
}
