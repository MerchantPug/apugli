package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.power.MobsIgnorePower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> type, World level) {
        super(type, level);
    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"))
    private LivingEntity modifyTarget(LivingEntity target) {
        if (world.isClient() || !(target instanceof PlayerEntity)) {
            return target;
        }

        List<MobsIgnorePower> powers = PowerHolderComponent.getPowers(target, MobsIgnorePower.class);
        boolean shouldIgnore = powers.stream().anyMatch(power -> power.shouldIgnore(this));

        return shouldIgnore ? null : target;
    }
}