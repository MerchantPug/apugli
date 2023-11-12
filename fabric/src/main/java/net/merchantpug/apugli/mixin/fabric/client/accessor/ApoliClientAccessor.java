package net.merchantpug.apugli.mixin.fabric.client.accessor;

import io.github.apace100.apoli.ApoliClient;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(ApoliClient.class)
public interface ApoliClientAccessor {
    @Accessor(value = "idToKeyBindingMap", remap = false)
    static HashMap<String, KeyMapping> apugli$getIdToKeybindingMap() {
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