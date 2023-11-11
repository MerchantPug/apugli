package net.merchantpug.apugli.mixin.fabric.common.accessor;

import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PowerTypeRegistry.class)
public interface PowerTypeRegistryAccessor {
    @Invoker("remove")
    static void apugli$invokeRemove(ResourceLocation id) {
        throw new AssertionError();
    }
}
