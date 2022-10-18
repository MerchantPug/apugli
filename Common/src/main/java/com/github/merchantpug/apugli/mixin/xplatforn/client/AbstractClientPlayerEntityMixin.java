package com.github.merchantpug.apugli.mixin.xplatforn.client;

import com.mojang.authlib.GameProfile;
import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import the.great.migration.merchantpug.apugli.power.PlayerModelTypePower;
import the.great.migration.merchantpug.apugli.power.SetTexturePower;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin extends Player {
    public AbstractClientPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable ProfilePublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void getModel(CallbackInfoReturnable<String> cir) {
        List<PlayerModelTypePower> playerModelTypePowers = PowerHolderComponent.getPowers(this, PlayerModelTypePower.class);
        List<SetTexturePower> setTexturePowers = PowerHolderComponent.getPowers(this, SetTexturePower.class).stream().filter(power -> power.model != null).toList();
        if(playerModelTypePowers.size() + setTexturePowers.size() > 1) {
            Apoli.LOGGER.warn("Entity " + this.getDisplayName().toString() + " has two instances of player model setting powers active.");
        }
        if(playerModelTypePowers.size() > 0) {
            cir.setReturnValue(PowerHolderComponent.getPowers(this, PlayerModelTypePower.class).get(0).model.toString());
        } else if(setTexturePowers.size() > 0) {
            cir.setReturnValue(PowerHolderComponent.getPowers(this, SetTexturePower.class).get(0).model.toString());
        }
    }
}
