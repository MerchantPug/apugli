package net.merchantpug.apugli.mixin.fabric.client;

import com.mojang.authlib.GameProfile;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {
    @Shadow public abstract boolean isUnderWater();

    @Shadow public Input input;

    public ClientPlayerEntityMixin(ClientLevel clientLevel, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(clientLevel, gameProfile, profilePublicKey);
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "STORE", ordinal = 0), ordinal = 5)
    private boolean resetPowerSprinting(boolean value) {
        if (Services.POWER.hasPower(this, ApugliPowers.SPRINTING.get()) && !this.isUnderWater() && (!this.input.hasForwardImpulse() || this.horizontalCollision && !this.minorHorizontalCollision)) {
            return true;
        }
        return !Services.POWER.hasPower(this, ApugliPowers.SPRINTING.get()) && value;
    }

}
