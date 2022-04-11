package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.ModifyMobBehaviorPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Angerable.class)
public interface AngerableMixin {
    @Shadow void stopAnger();

    @Inject(method = "tickAngerLogic", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/Angerable;getAngryAt()Ljava/util/UUID;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void stopAngerOnTargets(ServerWorld world, boolean angerPersistent, CallbackInfo ci, LivingEntity livingEntity) {
        List<ModifyMobBehaviorPower> modifyMobBehaviorPowers = PowerHolderComponent.getPowers(livingEntity, ModifyMobBehaviorPower.class);
        boolean shouldMakePassive = modifyMobBehaviorPowers.stream().anyMatch(power -> power.doesApply(livingEntity, (MobEntity)this) && power.getMobBehavior().isPassive((MobEntity)(Object)this, livingEntity));

        if (shouldMakePassive) {
            this.stopAnger();
        }
    }
}
