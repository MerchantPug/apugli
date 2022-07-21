package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.powers.ModifyBreedingCooldownPower;
import com.github.merchantpug.apugli.powers.PreventBreedingPower;
import com.github.merchantpug.apugli.util.ModComponents;
import io.github.apace100.origins.component.OriginComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(AnimalEntity.class)
public abstract class AnimalEntityMixin extends PassiveEntity {
    @Shadow public abstract boolean isBreedingItem(ItemStack stack);

    @Shadow public abstract boolean canEat();

    @Shadow private int loveTicks;

    protected AnimalEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void preventMobBreeding(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir, ItemStack itemStack) {
        List<PreventBreedingPower> preventBreedingPowerList = ModComponents.getOriginComponent(player).getPowers(PreventBreedingPower.class).stream().filter(power -> power.doesApply(this)).collect(Collectors.toList());
        if (preventBreedingPowerList.isEmpty()) return;
        if (this.isBreedingItem(itemStack)) {
            int i = this.getBreedingAge();
            if (i == 0 && this.canEat()) {
                if (preventBreedingPowerList.stream().anyMatch(PreventBreedingPower::hasActions)) {
                    preventBreedingPowerList.forEach(power -> power.executeAction(this));
                    this.loveTicks = (int)OriginComponent.modify(player, ModifyBreedingCooldownPower.class, 600);
                    cir.setReturnValue(ActionResult.SUCCESS);
                } else {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            }
        }
    }
}
