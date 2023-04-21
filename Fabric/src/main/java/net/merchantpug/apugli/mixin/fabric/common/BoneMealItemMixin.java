package net.merchantpug.apugli.mixin.fabric.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult;sidedSuccess(Z)Lnet/minecraft/world/InteractionResult;"))
    private void executeBoneMealAction(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Services.POWER.getPowers(context.getPlayer(), ApugliPowers.ACTION_ON_BONEMEAL.get())
                .stream()
                .filter(p -> p.doesApply(new BlockInWorld(context.getLevel(), context.getClickedPos(), true)))
                .forEach(p -> p.executeActions(context.getLevel(), context.getClickedPos(), context.getClickedFace()));
    }
}
