package net.merchantpug.apugli.mixin.xplatform.client;

import net.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundSystemMixin {
    @Shadow private boolean loaded;

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void apugli$cancelSound(SoundInstance sound, CallbackInfo ci) {
        if(this.loaded && sound instanceof AbstractSoundInstance abstractSound && Services.POWER.getPowers(Minecraft.getInstance().player, ApugliPowers.PREVENT_SOUND.get()).stream().anyMatch(power -> (power.doesApplyToCategory(sound.getSource()) || power.doesApplyToSound(((AbstractSoundInstanceAccess)abstractSound).apugli$getSoundEvent())) && power.isSoundNotWhitelisted(((AbstractSoundInstanceAccess)abstractSound).apugli$getSoundEvent()))) {
            ci.cancel();
        }
    }
}
