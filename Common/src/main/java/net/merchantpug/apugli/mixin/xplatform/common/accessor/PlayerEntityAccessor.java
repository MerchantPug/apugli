package net.merchantpug.apugli.mixin.xplatform.common.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface PlayerEntityAccessor {
    @Accessor("DATA_PLAYER_MODE_CUSTOMISATION") @Final
    static EntityDataAccessor<Byte> getPlayerModelParts() {
        throw new IllegalStateException();
    }
}
