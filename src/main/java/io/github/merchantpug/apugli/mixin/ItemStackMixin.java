package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.EdibleItemStackPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void executeEntityActions(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(user != null) {
            PowerHolderComponent.KEY.get(user).getPowers(EdibleItemStackPower.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(EdibleItemStackPower::eat);
        }
    }
}
