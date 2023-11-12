package net.merchantpug.apugli.mixin.xplatform.client;

import com.mojang.authlib.GameProfile;
import net.merchantpug.apugli.access.PlayerSkinAccess;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    @Shadow private @Nullable PlayerInfo playerInfo;

    public AbstractClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "getPlayerInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;getPlayerInfo(Ljava/util/UUID;)Lnet/minecraft/client/multiplayer/PlayerInfo;", shift = At.Shift.BY, by = 2))
    private void apugli$setPlayerToSkin(CallbackInfoReturnable<PlayerInfo> cir) {
        ((PlayerSkinAccess)(Object)this.playerInfo.getSkin()).apugli$setPlayer(this);
    }
}
