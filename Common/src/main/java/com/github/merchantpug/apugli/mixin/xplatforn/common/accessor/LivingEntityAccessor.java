package com.github.merchantpug.apugli.mixin.xplatforn.common.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("isOnSoulSpeedBlock")
    boolean invokeIsOnSoulSpeedBlock();
}
