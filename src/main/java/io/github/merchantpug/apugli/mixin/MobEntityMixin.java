package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.access.MobEntityAccess;
import io.github.merchantpug.apugli.behavior.MobBehavior;
import io.github.merchantpug.apugli.power.MobsIgnorePower;
import io.github.merchantpug.apugli.power.ModifyMobBehaviorPower;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements MobEntityAccess {

    protected MobEntityMixin(EntityType<? extends LivingEntity> type, World level) {
        super(type, level);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void pacifyAngerable(CallbackInfo ci) {

    }

    @ModifyVariable(method = "setTarget", at = @At("HEAD"))
    private LivingEntity modifyTarget(LivingEntity target) {
        if (world.isClient() || target == null) {
            return target;
        }

        List<ModifyMobBehaviorPower> modifyMobBehaviorPowers = PowerHolderComponent.getPowers(target, ModifyMobBehaviorPower.class);
        boolean shouldMakePassive = modifyMobBehaviorPowers.stream().anyMatch(power -> power.doesApply(target, (MobEntity)(Object)this) && power.getMobBehavior().isPassive((MobEntity)(Object)this, target));

        if (shouldMakePassive) {
            return null;
        }

        List<MobsIgnorePower> mobsIgnorePowers = PowerHolderComponent.getPowers(target, MobsIgnorePower.class);
        boolean shouldIgnore = mobsIgnorePowers.stream().anyMatch(power -> power.shouldIgnore(this));

        return shouldIgnore ? null : target;
    }

    @Override
    protected void applyDamage(DamageSource source, float amount) {
        if (source.getAttacker() != null) {
            PowerHolderComponent.getPowers(source.getAttacker(), ModifyMobBehaviorPower.class).forEach(power -> {
                power.getMobBehavior().onMobDamage((MobEntity)(Object)this, source.getAttacker());
            });
        }
        super.applyDamage(source, amount);
    }

    private final List<Pair<MobBehavior, Goal>> modifiedTargetSelectorGoals = new ArrayList<>();
    private final List<Pair<MobBehavior, Goal>> modifiedGoalSelectorGoals = new ArrayList<>();

    @Override
    public List<Pair<MobBehavior, Goal>> getModifiedTargetSelectorGoals() {
        return modifiedTargetSelectorGoals;
    }

    @Override
    public List<Pair<MobBehavior, Goal>> getModifiedGoalSelectorGoals() {
        return modifiedGoalSelectorGoals;
    }
}