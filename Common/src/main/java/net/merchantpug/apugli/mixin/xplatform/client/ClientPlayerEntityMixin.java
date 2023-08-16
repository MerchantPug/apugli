package net.merchantpug.apugli.mixin.xplatform.client;

import com.mojang.authlib.GameProfile;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {

    @Shadow @Final protected Minecraft minecraft;

    @Shadow public abstract void setSprinting(boolean pSprinting);

    @Shadow public Input input;

    public ClientPlayerEntityMixin(ClientLevel clientLevel, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(clientLevel, gameProfile, profilePublicKey);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSprinting()Z", ordinal = 1))
    private void allowPowerSprinting(CallbackInfo ci) {
        if (Services.POWER.hasPower(this, ApugliPowers.SPRINTING.get()) && !this.isSprinting() && this.input.hasForwardImpulse() && !this.isUnderWater()) {
              this.setSprinting(true);
        }
    }

}
