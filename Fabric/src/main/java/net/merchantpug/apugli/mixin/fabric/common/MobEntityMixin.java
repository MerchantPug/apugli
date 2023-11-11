package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.MobsIgnorePower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"), argsOnly = true)
    private LivingEntity apugli$modifyTarget(LivingEntity target) {
        if(level().isClientSide() || !(target instanceof Player)) {
            return target;
        }

        List<MobsIgnorePower> powers = Services.POWER.getPowers(target, ApugliPowers.MOBS_IGNORE.get());
        boolean shouldIgnore = powers.stream().anyMatch(power -> power.shouldIgnore(this));

        return shouldIgnore ? null : target;
    }
}