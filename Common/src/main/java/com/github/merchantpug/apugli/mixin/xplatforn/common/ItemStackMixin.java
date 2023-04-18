<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.power.ActionOnDurabilityChangePower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common;

import com.github.merchantpug.apugli.access.ItemStackAccess;
import com.github.merchantpug.apugli.mixin.xplatforn.common.accessor.BucketItemAccessor;
import com.github.merchantpug.apugli.mixin.xplatforn.common.accessor.ItemAccessor;
import the.great.migration.merchantpug.apugli.power.ActionOnDurabilityChange;
import the.great.migration.merchantpug.apugli.power.EdibleItemPower;
import com.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.item.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
@Implements(@Interface(iface = ItemStackAccess.class, prefix = "apugli$"))
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    @Shadow public abstract ItemStack copy();

    @Unique
    public Entity apugli$entity;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
    private void cacheEntity(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (this.apugli$getEntity() == null) this.apugli$setEntity(entity);
========
    private void cacheEntity(Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if(this.getEntity() == null) this.setEntity(entity);
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setBobbingAnimationTime(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void copyNewParams(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
        if (this.apugli$getEntity() != null) {
            ((ItemStackAccess) (Object) itemStack).setEntity(this.apugli$getEntity());
========
        if(this.getEntity() != null) {
            ((ItemStackAccess) (Object) itemStack).setEntity(this.getEntity());
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
        }
    }

    public void apugli$setEntity(Entity entity) { this.apugli$entity = entity; }

    public Entity apugli$getEntity() {
        return this.apugli$entity;
    }

    @Unique
    private FoodComponent apugli$stackFoodComponent;

    @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I", ordinal = 1))
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
    private void executeActionOnDurabilityDecrease(int amount, net.minecraft.util.math.random.Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(player, ActionOnDurabilityChangePower.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChangePower::executeDecreaseAction);
========
    private void executeActionOnDurabilityDecrease(int amount, net.minecraft.util.RandomSource random, ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(player, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeDecreaseAction);
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
    }

    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private <T extends LivingEntity> void executeActionBroken(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
        PowerHolderComponent.getPowers(entity, ActionOnDurabilityChangePower.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChangePower::executeBreakAction);
    }

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (user.canConsume(power.get().foodComponent.isAlwaysEdible())) {
                user.setCurrentHand(hand);
                if (this.getItem() instanceof BucketItem) {
                    BlockHitResult blockHitResult = ItemAccessor.callRaycast(world, user, ((BucketItemAccessor)this.getItem()).getFluid() == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) return;
========
    @Unique
    protected FoodProperties stackFoodComponent;
    @Unique
    protected UseAnim useAction;
    @Unique
    protected ItemStack returnStack;
    @Unique
    protected SoundEvent eatSound;

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if(this.isItemStackFood()) {
            ItemStack itemStack = user.getItemInHand(hand);
            if(user.canEat(this.getItemStackFoodComponent().canAlwaysEat())) {
                user.startUsingItem(hand);
                if(this.getItem() instanceof BucketItem) {
                    BlockHitResult blockHitResult = ItemAccessor.callRaycast(world, user, ((BucketItemAccessor)this.getItem()).getFluid() == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
                    if(blockHitResult.getType() == HitResult.Type.BLOCK) return;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
                }
                cir.setReturnValue(InteractionResultHolder.consume(itemStack));
            }
        }
    }

    @Inject(method = "finishUsing", at = @At("RETURN"), cancellable = true)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
    private void finishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            ItemStack newStack = this.copy();
            ((ItemStackAccess)(Object)newStack).setItemStackFoodComponent(power.get().foodComponent);
            newStack = user.eatFood(world, newStack);
            if (user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
                if (power.get().returnStack != null && newStack.isEmpty()) {
                    cir.setReturnValue(power.get().returnStack.copy());
========
    private void finishUsing(Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(this.isItemStackFood()) {
            ItemStack newStack = this.copy();
            newStack = user.eat(world, newStack);
            if(!((Player)user).getAbilities().instabuild) {
                if(((ItemStackAccess)(Object)newStack).getReturnStack() != null) {
                    cir.setReturnValue(((ItemStackAccess)(Object)newStack).getReturnStack());
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
                } else {
                    if (power.get().returnStack != null) {
                        ItemStack stack2 = power.get().returnStack.copy();
                        if (!player.giveItemStack(stack2)) {
                            player.dropItem(stack2, false);
                        }
                    }
                    cir.setReturnValue(newStack);
                }
            } else {
                cir.setReturnValue(newStack);
            }
            PowerHolderComponent.KEY.get(user).getPowers(EdibleItemPower.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(EdibleItemPower::eat);
        }
    }

    @Inject(method = "getUseAction", at = @At("HEAD"), cancellable = true)
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
    private void getUseAction(CallbackInfoReturnable<UseAction> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.useAction != null).findFirst();
        if (power.isPresent()) {
            this.apugli$setItemStackFoodComponent(power.get().foodComponent);
            cir.setReturnValue(power.get().useAction.equals(UseAction.DRINK) ? power.get().useAction : UseAction.EAT);
========
    private void getUseAction(CallbackInfoReturnable<UseAnim> cir) {
        if(this.isItemStackFood()) {
            cir.setReturnValue(this.getFoodUseAction() != null ? this.getFoodUseAction() : UseAnim.EAT);
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
        }
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void getMaxUseTime(CallbackInfoReturnable<Integer> cir) {
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.foodComponent.isSnack() ? 16 : 32));
========
        if(this.isItemStackFood()) {
            cir.setReturnValue(this.getItemStackFoodComponent().isFastFood() ? 16 : 32);
        }
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
    }

    @Inject(method = "getDrinkSound", at = @At("HEAD"), cancellable = true)
    private void getDrinkSound(CallbackInfoReturnable<SoundEvent> cir) {
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.sound != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.sound));
========
        if(this.getStackEatSound() != null) {
            cir.setReturnValue(this.getStackEatSound());
        }
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
    }

    @Inject(method = "getEatSound", at = @At("HEAD"), cancellable = true)
    private void getEatSound(CallbackInfoReturnable<SoundEvent> cir) {
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/ItemStackMixin.java
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.sound != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.sound));
    }

    public FoodComponent apugli$getItemStackFoodComponent() {
        return this.apugli$stackFoodComponent;
    }

    public void apugli$setItemStackFoodComponent(FoodComponent value) {
        this.apugli$stackFoodComponent = value;
========
        if(this.getStackEatSound() != null) {
            cir.setReturnValue(this.getStackEatSound());
        }
    }

    @Override
    public FoodProperties getItemStackFoodComponent() {
        return this.stackFoodComponent;
    }

    @Override
    public void setItemStackFoodComponent(FoodProperties stackFoodComponent) {
        this.stackFoodComponent = stackFoodComponent;
    }

    @Override
    public boolean isItemStackFood() {
        return this.stackFoodComponent != null;
    }

    @Override
    public UseAnim getFoodUseAction() {
        return this.useAction;
    }

    @Override
    public void setFoodUseAction(UseAnim useAction) {
        if(useAction == UseAnim.EAT || useAction == UseAnim.DRINK) {
            this.useAction = useAction;
        }
    }

    @Override
    public ItemStack getReturnStack() {
        return this.returnStack;
    }

    @Override
    public void setReturnStack(ItemStack stack) {
        this.returnStack = stack;
    }

    @Override
    public SoundEvent getStackEatSound() {
        return this.eatSound;
    }

    @Override
    public void setStackEatSound(SoundEvent sound) {
        this.eatSound = sound;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/ItemStackMixin.java
    }
}
