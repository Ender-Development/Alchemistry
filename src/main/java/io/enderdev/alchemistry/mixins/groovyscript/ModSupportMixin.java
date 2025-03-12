package io.enderdev.alchemistry.mixins.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.InternalModContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.enderdev.alchemistry.Tags;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ModSupport.class, remap = false)
public class ModSupportMixin {
    @WrapMethod(method = "registerContainer(Lcom/cleanroommc/groovyscript/compat/mods/GroovyContainer;)V")
    private void registerContainer(GroovyContainer<?> container, Operation<Void> original) {
        if (container instanceof InternalModContainer && container.getModId().equals(Tags.MOD_ID)) {
            return;
        }
        original.call(container);
    }
}
