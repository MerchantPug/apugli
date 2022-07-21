package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PositionedSoundInstance.class)
public class PositionedSoundInstanceMixin {
    @Inject(method = "<init>(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZILnet/minecraft/client/sound/SoundInstance$AttenuationType;DDD)V", at = @At(value = "TAIL"))
    private void captureSoundEvent(SoundEvent soundEvent, SoundCategory soundCategory, float f, float g, boolean bl, int i, SoundInstance.AttenuationType attenuationType, double d, double e, double h, CallbackInfo ci) {
        ((AbstractSoundInstanceAccess)(Object)this).setSoundEvent(soundEvent);
    }
}