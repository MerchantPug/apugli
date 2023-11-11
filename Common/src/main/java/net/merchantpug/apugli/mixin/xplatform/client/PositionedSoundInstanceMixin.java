package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleSoundInstance.class)
public class PositionedSoundInstanceMixin {
    @Inject(method = "<init>(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDD)V", at = @At(value = "TAIL"))
    private void apugli$captureSoundEvent(SoundEvent sound, SoundSource category, float volume, float pitch, RandomSource random, boolean repeat, int repeatDelay, SoundInstance.Attenuation attenuationType, double x, double y, double z, CallbackInfo ci) {
        ((AbstractSoundInstanceAccess)this).apugli$setSoundEvent(sound);
    }
}
