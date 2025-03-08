package al132.alchemistry.mixins.minecraft;

import al132.alchemistry.client.OverlayRenderer;
import al132.alchemistry.items.ItemElement;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class RenderItemMixin {
    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value = "RETURN"))
    private void renderItem(ItemStack stack, IBakedModel model, CallbackInfo ci) {
        if (stack.isEmpty()) return;
        Item item = stack.getItem();
        if (item instanceof ItemElement) {
            OverlayRenderer.INSTANCE.renderItem(stack.getMetadata());
        }
    }
}
