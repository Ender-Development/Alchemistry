package io.enderdev.alchemistry.client.gui.renderer

import io.enderdev.alchemistry.client.gui.GuiReactorModifiers
import io.enderdev.alchemistry.utils.RenderUtils
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

class FluidRenderer(val fluid: Fluid, val self: GuiReactorModifiers) : IRenderer {
	override val textureX = 175
	val stack = FluidStack(fluid, 1)
	override fun render(x: Int, y: Int, offY: Int) =
		RenderUtils.renderGuiTank(stack, 1, 1, x + 8.0, y + offY.toDouble(), 1.0, 16.0, 16.0)

	override fun renderTooltip(mouseX: Int, mouseY: Int) =
		self.drawHoveringText(fluid.getLocalizedName(stack), mouseX, mouseY)
}
