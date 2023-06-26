package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
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

import java.util.Optional;

@Mixin(ItemStack.class)
@Implements(@Interface(iface = ItemStackAccess.class, prefix = "apugli$"))
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract CompoundTag getOrCreateTag();

    @Shadow public abstract int getMaxDamage();

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

    @Unique
    private int apugli$previousDamage;

    @Inject(method = "setDamageValue", at = @At(value = "HEAD"))
    private void captureDamageValue(int damage, CallbackInfo ci) {
        CompoundTag tag = this.getOrCreateTag();
        apugli$previousDamage = tag.contains("Damage", Tag.TAG_INT) ? tag.getInt("Damage") : 0;
    }

    @Inject(method = "setDamageValue", at = @At(value = "TAIL"))
    private void executeDurabilityChangeActions(int newDamage, CallbackInfo ci) {
        if (!(apugli$getEntity() instanceof LivingEntity living)) return;
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

    public void apugli$setEntity(Entity entity) { this.apugli$entity = entity; }

    public Entity apugli$getEntity() {
        return this.apugli$entity;
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void getUseAction(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (!(((ItemStackAccess)(Object)stack).getEntity() instanceof LivingEntity living)) return;
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(living.level(), stack) && p.getUseAction() != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.getUseAction().equals(UseAnim.DRINK) ? edibleItemPower.getUseAction() : UseAnim.EAT));
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void getMaxUseTime(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (!(((ItemStackAccess)(Object)stack).getEntity() instanceof LivingEntity living)) return;
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(living.level(), stack)).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.getFoodComponent().isFastFood() ? 16 : 32));
    }

    @Inject(method = "getDrinkingSound", at = @At("HEAD"), cancellable = true)
    private void getDrinkSound(CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (!(((ItemStackAccess)(Object)stack).getEntity() instanceof LivingEntity living)) return;
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(living.level(), stack) && p.getSound() != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.getSound()));
    }

    @Inject(method = "getEatingSound", at = @At("HEAD"), cancellable = true)
    private void getEatSound(CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (!(((ItemStackAccess)(Object)stack).getEntity() instanceof LivingEntity living)) return;
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(living.level(), stack) && p.getSound() != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.getSound()));
    }

}