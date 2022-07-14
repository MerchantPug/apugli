package io.github.merchantpug.apugli.util.forge;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.registry.ModComponentsArchitectury;
import net.minecraft.entity.player.PlayerEntity;

public class ModComponentsImpl {
    public static OriginComponent getOriginComponent(PlayerEntity player) {
        return ModComponentsArchitectury.getOriginComponent(player);
    }
}
