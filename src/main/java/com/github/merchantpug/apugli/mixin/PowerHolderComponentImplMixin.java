package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.component.PowerHolderComponentImpl;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
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
    private void resetKeysToCheckWhenRemoved(PowerType<?> powerType, Identifier source, CallbackInfo ci) {
        if (!(owner instanceof ServerPlayerEntity)) return;
        Apugli.keysToCheck.remove(owner.getUuid());
    }
}
