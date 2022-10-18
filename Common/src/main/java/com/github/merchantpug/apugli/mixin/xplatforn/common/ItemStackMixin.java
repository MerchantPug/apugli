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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Consumer;

@Mixin(value = ItemStack.class, priority = 998)
public abstract class ItemStackMixin implements ItemStackAccess {
    @Shadow public abstract Item getItem();

    @Shadow public abstract ItemStack copy();

    @Unique
    public Entity entity;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void cacheEntity(Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if(this.getEntity() == null) this.setEntity(entity);
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setBobbingAnimationTime(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void copyNewParams(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        if(this.getEntity() != null) {
            ((ItemStackAccess) (Object) itemStack).setEntity(this.getEntity());
        }
        ItemStackFoodComponentUtil.setStackFood(itemStack, this.stackFoodComponent, this.useAction, this.returnStack, this.eatSound);
    }

    public void setEntity(Entity entity) { this.entity = entity; }

    public Entity getEntity() {
        return this.entity;
    }

    @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I", ordinal = 1))
    private void executeActionOnDurabilityDecrease(int amount, net.minecraft.util.RandomSource random, ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(player, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeDecreaseAction);
    }

    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private <T extends LivingEntity> void executeActionBroken(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
        PowerHolderComponent.getPowers(entity, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeBreakAction);
    }

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
                }
                cir.setReturnValue(InteractionResultHolder.consume(itemStack));
            }
        }
    }

    @Inject(method = "finishUsing", at = @At("RETURN"), cancellable = true)
    private void finishUsing(Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(this.isItemStackFood()) {
            ItemStack newStack = this.copy();
            newStack = user.eat(world, newStack);
            if(!((Player)user).getAbilities().instabuild) {
                if(((ItemStackAccess)(Object)newStack).getReturnStack() != null) {
                    cir.setReturnValue(((ItemStackAccess)(Object)newStack).getReturnStack());
                } else {
                    cir.setReturnValue(newStack);
                }
            }
            PowerHolderComponent.KEY.get(user).getPowers(EdibleItemPower.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(EdibleItemPower::eat);
        }
    }

    @Inject(method = "getUseAction", at = @At("HEAD"), cancellable = true)
    private void getUseAction(CallbackInfoReturnable<UseAnim> cir) {
        if(this.isItemStackFood()) {
            cir.setReturnValue(this.getFoodUseAction() != null ? this.getFoodUseAction() : UseAnim.EAT);
        }
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void getMaxUseTime(CallbackInfoReturnable<Integer> cir) {
        if(this.isItemStackFood()) {
            cir.setReturnValue(this.getItemStackFoodComponent().isFastFood() ? 16 : 32);
        }
    }

    @Inject(method = "getDrinkSound", at = @At("HEAD"), cancellable = true)
    private void getDrinkSound(CallbackInfoReturnable<SoundEvent> cir) {
        if(this.getStackEatSound() != null) {
            cir.setReturnValue(this.getStackEatSound());
        }
    }

    @Inject(method = "getEatSound", at = @At("HEAD"), cancellable = true)
    private void getEatSound(CallbackInfoReturnable<SoundEvent> cir) {
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
    }
}
