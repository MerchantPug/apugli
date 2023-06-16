package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleSoundInstance.class)
public class PositionedSoundInstanceMixin {
    @Inject(method = "<init>(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDD)V", at = @At(value = "TAIL"))
    private void captureSoundEvent(SoundEvent sound, SoundSource soundSource, float f, float g, boolean bl, int i, SoundInstance.Attenuation attenuation, double d, double e, double h, CallbackInfo ci) {
        ((AbstractSoundInstanceAccess)this).setSoundEvent(sound);
    }
}
