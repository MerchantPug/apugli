package com.github.merchantpug.apugli.access;

import net.minecraft.entity.player.PlayerEntity;

public interface AnimalModelAccess {
    PlayerEntity apugli$getPlayerEntity();
    void apugli$setPlayerEntity(PlayerEntity playerEntity);

    boolean apugli$isHidden();
    void apugli$setHidden(boolean value);
}
