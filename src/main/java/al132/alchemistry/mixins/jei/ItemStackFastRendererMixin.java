package al132.alchemistry.mixins.jei;

import al132.alchemistry.client.OverlayRenderer;
import al132.alchemistry.items.ItemElement;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mezz.jei.render.ItemStackFastRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(value = ItemStackFastRenderer.class, remap = false)
public abstract class ItemStackFastRendererMixin {
    @Shadow
    public static FontRenderer getFontRenderer(ItemStack itemStack) {
        return null;
    }

    @WrapMethod(method = "renderOverlay(Lnet/minecraft/item/ItemStack;Ljava/awt/Rectangle;I)V")
    private void renderOverlay(ItemStack itemStack, Rectangle area, int padding, Operation<Void> original) {
        if (itemStack.getItem() instanceof ItemElement) {
            FontRenderer fontRenderer = getFontRenderer(itemStack);
            OverlayRenderer.INSTANCE.renderJeiOverlay(fontRenderer, itemStack.getMetadata(), area.x + padding, area.y + padding);
        } else {
            original.call(itemStack, area, padding);
        }
    }
}
