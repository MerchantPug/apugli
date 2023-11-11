package net.merchantpug.apugli.mixin.forge.common.accessor;

import io.github.edwinmindcraft.apoli.client.ApoliClientEventHandler;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(ApoliClientEventHandler.class)
public interface ApoliClientEventHandlerAccessor {

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
