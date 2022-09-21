package net.merchantpug.apugli.mixin.client;

import net.merchantpug.apugli.access.AbstractSoundInstanceAccess;
import net.merchantpug.apugli.power.PreventSoundPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
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
        if (sound instanceof AbstractSoundInstance abstractSound && PowerHolderComponent.getPowers(MinecraftClient.getInstance().player, PreventSoundPower.class).stream().anyMatch(power -> (power.doesApplyToCategory(sound.getCategory()) || power.doesApplyToSound(((AbstractSoundInstanceAccess)abstractSound).getSoundEvent())) && power.isSoundNotWhitelisted(((AbstractSoundInstanceAccess)abstractSound).getSoundEvent()))) {
            ci.cancel();
        }
    }
}
