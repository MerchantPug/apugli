package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PositionedSoundInstance.class)
public class PositionedSoundInstanceMixin {
    @Inject(method = "<init>(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFLnet/minecraft/util/math/random/Random;ZILnet/minecraft/client/sound/SoundInstance$AttenuationType;DDD)V", at = @At(value = "TAIL"))
    private void captureSoundEvent(SoundEvent sound, SoundCategory category, float volume, float pitch, Random random, boolean repeat, int repeatDelay, SoundInstance.AttenuationType attenuationType, double x, double y, double z, CallbackInfo ci) {
        ((AbstractSoundInstanceAccess)this).setSoundEvent(sound);
    }
}
