package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void apugli$performLightningStruckActions(CallbackInfo ci, List list, Iterator var2, Entity entity) {
        if (entity instanceof LivingEntity living)
            Services.POWER.getPowers(living, ApugliPowers.ACTION_WHEN_LIGHTNING_STRUCK.get()).forEach(p -> ApugliPowers.ACTION_WHEN_LIGHTNING_STRUCK.get().execute(p, living, (LightningBolt)(Object)this));
    }

}
