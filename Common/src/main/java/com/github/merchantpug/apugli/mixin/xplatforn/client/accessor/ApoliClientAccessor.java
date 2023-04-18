<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/ApoliClientAccessor.java
package net.merchantpug.apugli.mixin.client;
========
package com.github.merchantpug.apugli.mixin.xplatforn.client.accessor;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/accessor/ApoliClientAccessor.java

import io.github.apace100.apoli.ApoliClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
@Mixin(ApoliClient.class)
public interface ApoliClientAccessor {
    @Accessor(value = "idToKeyBindingMap", remap = false)
    static HashMap<String, KeyMapping> getIdToKeyBindingMap() {
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