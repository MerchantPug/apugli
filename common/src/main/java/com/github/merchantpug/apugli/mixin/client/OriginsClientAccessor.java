package com.github.merchantpug.apugli.mixin.client;

import io.github.apace100.origins.OriginsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
@Mixin(OriginsClient.class)
public interface OriginsClientAccessor {
    @Accessor(value = "idToKeyBindingMap", remap = false)
    static HashMap<String, KeyBinding> getIdToKeyBindingMap() {
        throw new RuntimeException("");
    }

    @Accessor(value = "initializedKeyBindingMap", remap = false)
    static boolean getInitializedKeyBindingMap() {
        throw new RuntimeException("");
    }

    @Accessor(value = "initializedKeyBindingMap", remap = false)
    static void setInitializedKeyBindingMap(boolean value) {
        throw new RuntimeException("");
    }
}