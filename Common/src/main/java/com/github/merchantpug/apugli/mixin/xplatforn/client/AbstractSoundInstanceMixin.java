package com.github.merchantpug.apugli.mixin.xplatforn.client;

import com.github.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin implements AbstractSoundInstanceAccess {
    @Unique private SoundEvent apugli$soundEvent;

    @Inject(method = "<init>(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;Lnet/minecraft/util/math/random/Random;)V", at = @At("TAIL"))
    private void captureSoundEvent(SoundEvent sound, SoundSource category, RandomSource random, CallbackInfo ci) {
        apugli$soundEvent = sound;
    }

    @Override
    public void setSoundEvent(SoundEvent soundEvent) {
        apugli$soundEvent = soundEvent;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return apugli$soundEvent;
    }
}
