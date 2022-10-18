package com.github.merchantpug.apugli.mixin.xplatforn.client.accessor;

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
