package net.merchantpug.apugli.mixin.xplatform.client;

import io.github.apace100.apoli.Apoli;
import net.merchantpug.apugli.access.PlayerSkinAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.PlayerModelTypePower;
import net.merchantpug.apugli.power.SetTexturePower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerSkin.class)
public class PlayerSkinMixin implements PlayerSkinAccess {

    @Unique
    private Player apugli$player;

    @Inject(method = "model", at = @At("HEAD"), cancellable = true)
    private void apugli$getModel(CallbackInfoReturnable<String> cir) {
        List<PlayerModelTypePower> playerModelTypePowers = Services.POWER.getPowers(this.apugli$player, ApugliPowers.PLAYER_MODEL_TYPE.get());
        List<SetTexturePower> setTexturePowers = Services.POWER.getPowers(this.apugli$player, ApugliPowers.SET_TEXTURE.get()).stream().filter(p -> p.getModel() != null).toList();
        if(playerModelTypePowers.size() + setTexturePowers.size() > 1) {
            Apoli.LOGGER.warn("Entity " + this.apugli$player.getDisplayName() + " has two instances of player model setting powers active.");
        }
        if(!playerModelTypePowers.isEmpty()) {
            cir.setReturnValue(Services.POWER.getPowers(this.apugli$player, ApugliPowers.PLAYER_MODEL_TYPE.get()).get(0).getModel().toString());
        } else if (!setTexturePowers.isEmpty()) {
            cir.setReturnValue(Services.POWER.getPowers(this.apugli$player, ApugliPowers.SET_TEXTURE.get()).get(0).getModel().toString());
        }
    }

    @Override
    public void apugli$setPlayer(Player player) {
        this.apugli$player = player;
    }
}
