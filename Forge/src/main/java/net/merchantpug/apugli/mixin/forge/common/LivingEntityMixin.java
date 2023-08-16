package net.merchantpug.apugli.mixin.forge.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.util.CoreUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    ItemStack apugli$capturedStack;
    @Unique
    LivingEntity apugli$capturedEntity;

    @Inject(method = "addEatEffect", at = @At("HEAD"))
    private void captureEatLocals(ItemStack pFood, Level pLevel, LivingEntity pLivingEntity, CallbackInfo ci) {
        apugli$capturedStack = pFood;
        apugli$capturedEntity = pLivingEntity;
    }

    // We cannot use a ModifyExpressionValue from MixinExtras here because that does not seem to like non remapped methods.
    @ModifyExpressionValue(method = "addEatEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isEdible()Z"))
    private boolean isEdibleWithPower(boolean original) {
        if (CoreUtil.doEdibleItemPowersApply(apugli$capturedStack, apugli$capturedEntity)) {
            this.apugli$capturedStack = null;
            this.apugli$capturedEntity = null;
            return true;
        }
        this.apugli$capturedStack = null;
        this.apugli$capturedEntity = null;
        return original;
    }

}
