package al132.alchemistry.mixins.minecraft;

import al132.alchemistry.chemistry.ElementRegistry;
import al132.alchemistry.items.ItemElement;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class RenderItemMixin {
    @Inject(method = "renderItemOverlays", at = @At("HEAD"))
    private void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, CallbackInfo ci) {
        Item item = stack.getItem();
        if (item instanceof ItemElement) {
            fr.drawStringWithShadow(ElementRegistry.INSTANCE.get(stack.getMetadata()).getAbbreviation(), xPosition + 1, yPosition + 1, 0xFFFFFF);
        }
    }
}
