<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/MobEntityMixin.java
package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.power.MobsIgnorePower;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common;

import the.great.migration.merchantpug.apugli.power.MobsIgnorePower;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/MobEntityMixin.java
import io.github.apace100.apoli.component.PowerHolderComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"))
    private LivingEntity modifyTarget(LivingEntity target) {
        if(level.isClientSide() || !(target instanceof Player)) {
            return target;
        }

        List<MobsIgnorePower> powers = PowerHolderComponent.getPowers(target, MobsIgnorePower.class);
        boolean shouldIgnore = powers.stream().anyMatch(power -> power.shouldIgnore(this));

        return shouldIgnore ? null : target;
    }
}