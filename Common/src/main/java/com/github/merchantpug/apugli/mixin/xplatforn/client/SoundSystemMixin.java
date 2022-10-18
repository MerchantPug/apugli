package com.github.merchantpug.apugli.mixin.xplatforn.client;

import com.github.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import the.great.migration.merchantpug.apugli.power.PreventSoundPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SoundEngine.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void cancelSound(SoundInstance sound, CallbackInfo ci) {
        if(sound instanceof AbstractSoundInstance abstractSound && PowerHolderComponent.getPowers(Minecraft.getInstance().player, PreventSoundPower.class).stream().anyMatch(power -> (power.doesApplyToCategory(sound.getSource()) || power.doesApplyToSound(((AbstractSoundInstanceAccess)abstractSound).getSoundEvent())) && power.isSoundNotWhitelisted(((AbstractSoundInstanceAccess)abstractSound).getSoundEvent()))) {
            ci.cancel();
        }
    }
}
