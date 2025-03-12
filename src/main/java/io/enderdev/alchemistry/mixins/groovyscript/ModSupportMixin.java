package io.enderdev.alchemistry.mixins.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.enderdev.alchemistry.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModSupport.class, remap = false)
public class ModSupportMixin {
    @ModifyExpressionValue(
            method = "init",
            at = @At(value = "INVOKE", target = "Lcom/cleanroommc/groovyscript/compat/mods/GroovyContainer;isLoaded()Z")
    )
    private static boolean isModLoaded(boolean original, @Local GroovyContainer<?> container) {
        if (container.getModId().equals(Tags.MOD_ID)) {
            return false;
        }
        return original;
    }
}
