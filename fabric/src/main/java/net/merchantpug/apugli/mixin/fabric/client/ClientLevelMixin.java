package net.merchantpug.apugli.mixin.fabric.client;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.ClientActionOverTime;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    // This is injected here so it is run at the same time as Cardinal Components API.
    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void apugli$tickClientActions(Entity entity, CallbackInfo ci) {
        if (!(entity instanceof LivingEntity living)) return;
        Services.POWER.getPowers(living, ApugliPowers.CLIENT_ACTION_OVER_TIME.get()).forEach(ClientActionOverTime::clientTick);
    }
}
