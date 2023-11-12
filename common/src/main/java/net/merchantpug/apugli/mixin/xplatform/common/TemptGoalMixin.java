package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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
    @Shadow protected Player player;

    @Shadow @Final protected PathfinderMob mob;

    @Inject(method = "canUse", at = @At(value = "RETURN", opcode = 1), cancellable = true)
    private void apugli$stopStartingIfTargetCannotBreed(CallbackInfoReturnable<Boolean> cir) {
        if(Services.POWER.getPowers(this.player, ApugliPowers.PREVENT_BREEDING.get()).stream().anyMatch(power -> power.doesApply(this.mob) && power.preventFollow) && !((TemptGoal)(Object)this instanceof Cat.CatTemptGoal)) {
            cir.setReturnValue(false);
        }
    }

}
