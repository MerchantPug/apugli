package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract CompoundTag getOrCreateTag();

    @Shadow public abstract int getMaxDamage();

    @Unique
    private int apugli$previousDamage;

    @ModifyVariable(method = "setDamageValue", at = @At(value = "HEAD"), argsOnly = true)
    private int apugli$captureDamageValue(int newDamage) {
        if (Services.PLATFORM.getEntityFromItemStack((ItemStack)(Object)this) instanceof LivingEntity living) {
            CompoundTag tag = this.getOrCreateTag();
            apugli$previousDamage = tag.contains("Damage", Tag.TAG_INT) ? tag.getInt("Damage") : 0;
            int removedDurability = apugli$previousDamage - newDamage;
            for (Object power : Services.POWER.getPowers(living, ApugliPowers.MODIFY_DURABILITY_CHANGE.get())) {
                if (ApugliPowers.MODIFY_DURABILITY_CHANGE.get().doesApply(power, living.level(), (ItemStack)(Object)this, removedDurability)) {
                    removedDurability = ApugliPowers.MODIFY_DURABILITY_CHANGE.get().postFunction(power, Services.PLATFORM.applyModifiers(living, ApugliPowers.MODIFY_DURABILITY_CHANGE.get(), removedDurability));
                }
            }
            return apugli$previousDamage - removedDurability;
        }
        return newDamage;
    }

    @Inject(method = "setDamageValue", at = @At(value = "TAIL"))
    private void apugli$executeDurabilityChangeActions(int newDamage, CallbackInfo ci) {
        if (!(Services.PLATFORM.getEntityFromItemStack((ItemStack)(Object)this) instanceof LivingEntity living)) return;
        int addedDamage = newDamage - apugli$previousDamage;
        Services.POWER.getPowers(living, ApugliPowers.ACTION_ON_DURABILITY_CHANGE.get()).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(power -> {
            if (newDamage >= this.getMaxDamage()) {
                power.executeBreakAction((ItemStack)(Object)this);
            } else if (addedDamage > 0) {
                power.executeDecreaseAction((ItemStack)(Object)this);
            } else if (addedDamage < 0) {
                power.executeIncreaseAction((ItemStack)(Object)this);
            }
        });
    }

}