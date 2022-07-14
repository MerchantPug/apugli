package io.github.merchantpug.apugli.mixin.client;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.powers.PreventSoundPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V", at = @At("HEAD"), cancellable = true)
    private void cancelSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, CallbackInfo ci) {
        if (OriginComponent.getPowers(this.client.player, PreventSoundPower.class).stream().anyMatch(power -> (power.doesApplyToCategory(category) || power.doesApplyToSound(sound)) && power.isSoundNotWhitelisted(sound))) {
            ci.cancel();
        }
    }

    @Inject(method = "playSoundFromEntity", at = @At("HEAD"), cancellable = true)
    private void cancelSoundFromEntity(PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch, CallbackInfo ci) {
        if (OriginComponent.getPowers(this.client.player, PreventSoundPower.class).stream().anyMatch(power -> (power.doesApplyToCategory(category) || power.doesApplyToSound(sound)) && power.isSoundNotWhitelisted(sound))) {
            ci.cancel();
        }
    }
}
