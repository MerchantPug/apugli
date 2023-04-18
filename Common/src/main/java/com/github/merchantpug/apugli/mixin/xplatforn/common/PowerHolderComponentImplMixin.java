package com.github.merchantpug.apugli.mixin.xplatforn.common;

import the.great.migration.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.component.PowerHolderComponentImpl;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PowerHolderComponentImpl.class, remap = false)
public class PowerHolderComponentImplMixin {
    @Shadow @Final private LivingEntity owner;

    @Inject(method = "removePower", at = @At(value = "INVOKE", target = "Lio/github/apace100/apoli/power/Power;onLost()V"))
    private void resetKeysToCheckWhenRemoved(PowerType<?> powerType, ResourceLocation source, CallbackInfo ci) {
        if(!(owner instanceof ServerPlayer)) return;
        Apugli.keysToCheck.remove(owner.getUUID());
    }
}
