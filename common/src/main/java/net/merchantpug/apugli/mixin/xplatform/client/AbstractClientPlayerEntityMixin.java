package net.merchantpug.apugli.mixin.xplatform.client;

import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.Apoli;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.PlayerModelTypePower;
import net.merchantpug.apugli.power.SetTexturePower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin extends Player {

    public AbstractClientPlayerEntityMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "getModelName", at = @At("HEAD"), cancellable = true)
    private void apugli$getModel(CallbackInfoReturnable<String> cir) {
        List<PlayerModelTypePower> playerModelTypePowers = Services.POWER.getPowers(this, ApugliPowers.PLAYER_MODEL_TYPE.get());
        List<SetTexturePower> setTexturePowers = Services.POWER.getPowers(this, ApugliPowers.SET_TEXTURE.get()).stream().filter(p -> p.getModel() != null).toList();
        if(playerModelTypePowers.size() + setTexturePowers.size() > 1) {
            Apoli.LOGGER.warn("Entity " + this.getDisplayName() + " has two instances of player model setting powers active.");
        }
        if(playerModelTypePowers.size() > 0) {
            cir.setReturnValue(Services.POWER.getPowers(this, ApugliPowers.PLAYER_MODEL_TYPE.get()).get(0).getModel().toString());
        } else if (setTexturePowers.size() > 0) {
            cir.setReturnValue(Services.POWER.getPowers(this, ApugliPowers.SET_TEXTURE.get()).get(0).getModel().toString());
        }
    }
}