package com.github.merchantpug.apugli.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.component.OriginComponent;
import net.minecraft.entity.player.PlayerEntity;

public class ModComponents {
    @ExpectPlatform
    public static OriginComponent getOriginComponent(PlayerEntity player) {
        throw new AssertionError();
    }
}