package com.github.merchantpug.apugli.mixin.client;

import com.github.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin implements AbstractSoundInstanceAccess {
    @Unique private SoundEvent apugli$soundEvent;

    @Inject(method = "<init>(Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;Lnet/minecraft/util/math/random/Random;)V", at = @At("TAIL"))
    private void captureSoundEvent(SoundEvent sound, SoundCategory category, Random random, CallbackInfo ci) {
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
