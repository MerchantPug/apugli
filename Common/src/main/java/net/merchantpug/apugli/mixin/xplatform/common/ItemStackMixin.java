package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.ActionOnDurabilityChangePower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
@Implements(@Interface(iface = ItemStackAccess.class, prefix = "apugli$"))
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract CompoundTag getOrCreateTag();

    @Unique
    public Entity apugli$entity;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void cacheEntity(Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (this.apugli$getEntity() == null) this.apugli$setEntity(entity);
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setPopTime(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void copyNewParams(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        if (this.apugli$getEntity() != null) {
            ((ItemStackAccess) (Object) itemStack).setEntity(this.apugli$getEntity());
        }
    }

    @Inject(method = "setDamageValue", at = @At(value = "HEAD"))
    private void executeDurabilityChangeActions(int newDamage, CallbackInfo ci) {
        if (!(apugli$getEntity() instanceof LivingEntity living)) return;
        int previousDamage = this.getOrCreateTag().getInt("Damage");
        int damageDifference = newDamage - previousDamage;
        Services.POWER.getPowers(living, ApugliPowers.ACTION_ON_DURABILITY_CHANGE.get()).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(power -> {
            if (damageDifference > 0) {
                power.executeDecreaseAction();
            } else if (damageDifference < 0) {
                power.executeIncreaseAction();
            }
        });

    }

    public void apugli$setEntity(Entity entity) { this.apugli$entity = entity; }

    public Entity apugli$getEntity() {
        return this.apugli$entity;
    }


    @Inject(method = "hurtAndBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private <T extends LivingEntity> void executeActionBroken(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
        Services.POWER.getPowers(entity, ApugliPowers.ACTION_ON_DURABILITY_CHANGE.get()).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChangePower::executeBreakAction);
    }

}