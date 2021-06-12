package io.github.merchantpug.apugli.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccess {
    @Accessor("dead")
    boolean getDead();

    @Invoker("isOnSoulSpeedBlock")
    boolean invokeIsOnSoulSpeedBlock();
}
