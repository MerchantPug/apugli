package com.github.merchantpug.apugli.mixin.xplatforn.common;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import the.great.migration.merchantpug.apugli.power.ActionOnBoneMealPower;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;success(Z)Lnet/minecraft/util/ActionResult;"))
    private void executeBoneMealAction(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        PowerHolderComponent.getPowers(context.getPlayer(), ActionOnBoneMealPower.class)
                .stream()
                .filter(p -> p.doesApply(new BlockInWorld(context.getLevel(), context.getClickedPos(), true)))
                .forEach(p -> p.executeActions(context.getLevel(), context.getClickedPos(), context.getClickedFace()));
    }
}
