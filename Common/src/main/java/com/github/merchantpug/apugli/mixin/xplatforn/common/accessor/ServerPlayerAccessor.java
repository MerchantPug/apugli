package com.github.merchantpug.apugli.mixin.xplatforn.common.accessor;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {
    @Accessor()
    int getJoinInvulnerabilityTicks();
}
