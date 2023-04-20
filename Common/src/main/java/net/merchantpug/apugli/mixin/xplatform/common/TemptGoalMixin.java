package net.merchantpug.apugli.mixin.xplatform.common;

import the.great.migration.merchantpug.apugli.power.PreventBreedingPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TemptGoal.class)
public abstract class TemptGoalMixin extends Goal {
    @Shadow protected Player closestPlayer;

    @Shadow @Final protected PathfinderMob mob;

    @Inject(method = "canStart", at = @At(value = "RETURN", opcode = 1), cancellable = true)
    private void stopStartingIfTargetCannotBreed(CallbackInfoReturnable<Boolean> cir) {
        if(PowerHolderComponent.getPowers(this.closestPlayer, PreventBreedingPower.class).stream().anyMatch(power -> power.doesApply(this.mob) && power.preventFollow) && !((TemptGoal)(Object)this instanceof Cat.CatTemptGoal)) {
            cir.setReturnValue(false);
        }
    }
}
