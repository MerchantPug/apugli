package net.merchantpug.apugli.mixin.fabric.client;

import com.mojang.authlib.GameProfile;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.SprintingPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {
    @Shadow public abstract boolean isUnderWater();

    @Shadow public Input input;

    public ClientPlayerEntityMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprinting()Z"))
    private void apugli$allowPowerSprinting(CallbackInfo ci) {
        if (Services.POWER.hasPower(this, ApugliPowers.SPRINTING.get()) && !this.isSprinting() && this.isInWater() && !this.isUnderWater() && (Services.POWER.getPowers(this, ApugliPowers.SPRINTING.get()).stream().noneMatch(SprintingPower::requiresInput) || this.input.hasForwardImpulse())) {
            this.setSprinting(true);
        }
    }

    @ModifyVariable(method = "aiStep", at = @At(value = "STORE", ordinal = 1), ordinal = 5)
    private boolean apugli$resetPowerSprinting(boolean value) {
        if (Services.POWER.hasPower(this, ApugliPowers.SPRINTING.get())) {
            return Services.POWER.getPowers(this, ApugliPowers.SPRINTING.get()).stream().allMatch(SprintingPower::requiresInput) && (!this.input.hasForwardImpulse() || this.horizontalCollision && !this.minorHorizontalCollision);
        }
        return value;
    }

}
