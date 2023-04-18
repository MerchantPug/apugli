<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/client/ClientPlayerEntityAccessor.java
package net.merchantpug.apugli.mixin.client;
========
package com.github.merchantpug.apugli.mixin.xplatforn.client.accessor;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/client/accessor/ClientPlayerEntityAccessor.java

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(LocalPlayer.class)
public interface ClientPlayerEntityAccessor {
    @Accessor
    Minecraft getClient();
}
