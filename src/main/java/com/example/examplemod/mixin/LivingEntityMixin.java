package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.power.HoverPower;
import com.github.merchantpug.apugli.registry.ExamplePowerFactories;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void example$hover(CallbackInfo ci) {
        if(Services.PLATFORM.hasPower((LivingEntity)(Object)this, HoverPower.class, ExamplePowerFactories.HOVER)) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            this.fallDistance = 0.0F;
        }
    }
}