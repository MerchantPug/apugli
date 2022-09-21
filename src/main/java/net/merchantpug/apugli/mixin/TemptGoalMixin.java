package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.power.PreventBreedingPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TemptGoal.class)
public abstract class TemptGoalMixin extends Goal {
    @Shadow protected PlayerEntity closestPlayer;

    @Shadow @Final protected PathAwareEntity mob;

    @Inject(method = "canStart", at = @At(value = "RETURN", opcode = 1), cancellable = true)
    private void stopStartingIfTargetCannotBreed(CallbackInfoReturnable<Boolean> cir) {
        if (PowerHolderComponent.getPowers(this.closestPlayer, PreventBreedingPower.class).stream().anyMatch(power -> power.doesApply(this.mob) && power.preventFollow) && !((TemptGoal)(Object)this instanceof CatEntity.TemptGoal)) {
            cir.setReturnValue(false);
        }
    }
}
