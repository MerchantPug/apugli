package com.github.merchantpug.apugli.util.fabric;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;

public class ModComponentsImpl {
    public static OriginComponent getOriginComponent(PlayerEntity player) {
        return ModComponents.ORIGIN.get(player);
    }
}
