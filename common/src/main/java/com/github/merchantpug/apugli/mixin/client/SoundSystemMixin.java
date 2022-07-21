package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import com.github.merchantpug.apugli.powers.PreventSoundPower;
import io.github.apace100.origins.component.OriginComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void cancelSound(SoundInstance sound, CallbackInfo ci) {
        if (sound instanceof AbstractSoundInstance && OriginComponent.getPowers(MinecraftClient.getInstance().player, PreventSoundPower.class).stream().anyMatch(power -> (power.doesApplyToCategory(sound.getCategory()) || power.doesApplyToSound(((AbstractSoundInstanceAccess)sound).getSoundEvent())) && power.isSoundNotWhitelisted(((AbstractSoundInstanceAccess)sound).getSoundEvent()))) {
            ci.cancel();
        }
    }
}