package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.power.ActionOnDurabilityChange;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.util.ItemStackFoodComponentUtil;
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
    private void cacheEntity(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (this.getEntity() == null) this.setEntity(entity);
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setBobbingAnimationTime(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void copyNewParams(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        if (this.getEntity() != null) {
            ((ItemStackAccess) (Object) itemStack).setEntity(this.getEntity());
        }
        ItemStackFoodComponentUtil.setStackFood(itemStack, this.stackFoodComponent, this.useAction, this.returnStack, this.eatSound);
    }

    public void setEntity(Entity entity) { this.entity = entity; }

    public Entity getEntity() {
        return this.entity;
    }

    @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I", ordinal = 1))
    private void executeActionOnDurabilityDecrease(int amount, net.minecraft.util.math.random.Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(player, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeDecreaseAction);
    }

    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private <T extends LivingEntity> void executeActionBroken(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
        PowerHolderComponent.getPowers(entity, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeBreakAction);
    }

    @Unique
    protected FoodComponent stackFoodComponent;
    @Unique
    protected UseAction useAction;
    @Unique
    protected ItemStack returnStack;
    @Unique
    protected SoundEvent eatSound;

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (this.isItemStackFood()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (user.canConsume(this.getItemStackFoodComponent().isAlwaysEdible())) {
                user.setCurrentHand(hand);
                if (this.getItem() instanceof BucketItem) {
                    BlockHitResult blockHitResult = ItemAccessor.callRaycast(world, user, ((BucketItemAccessor)this.getItem()).getFluid() == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) return;
                }
                cir.setReturnValue(TypedActionResult.consume(itemStack));
            }
        }
    }

    @Inject(method = "finishUsing", at = @At("RETURN"), cancellable = true)
    private void finishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (this.isItemStackFood()) {
            ItemStack newStack = this.copy();
            newStack = user.eatFood(world, newStack);
            if (!((PlayerEntity)user).getAbilities().creativeMode) {
                if (((ItemStackAccess)(Object)newStack).getReturnStack() != null) {
                    cir.setReturnValue(((ItemStackAccess)(Object)newStack).getReturnStack());
                } else {
                    cir.setReturnValue(newStack);
                }
            }
            PowerHolderComponent.KEY.get(user).getPowers(EdibleItemPower.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(EdibleItemPower::eat);
        }
    }

    @Inject(method = "getUseAction", at = @At("HEAD"), cancellable = true)
    private void getUseAction(CallbackInfoReturnable<UseAction> cir) {
        if (this.isItemStackFood()) {
            cir.setReturnValue(this.getFoodUseAction() != null ? this.getFoodUseAction() : UseAction.EAT);
        }
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void getMaxUseTime(CallbackInfoReturnable<Integer> cir) {
        if (this.isItemStackFood()) {
            cir.setReturnValue(this.getItemStackFoodComponent().isSnack() ? 16 : 32);
        }
    }

    @Inject(method = "getDrinkSound", at = @At("HEAD"), cancellable = true)
    private void getDrinkSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (this.getStackEatSound() != null) {
            cir.setReturnValue(this.getStackEatSound());
        }
    }

    @Inject(method = "getEatSound", at = @At("HEAD"), cancellable = true)
    private void getEatSound(CallbackInfoReturnable<SoundEvent> cir) {
        if (this.getStackEatSound() != null) {
            cir.setReturnValue(this.getStackEatSound());
        }
    }

    @Override
    public FoodComponent getItemStackFoodComponent() {
        return this.stackFoodComponent;
    }

    @Override
    public void setItemStackFoodComponent(FoodComponent stackFoodComponent) {
        this.stackFoodComponent = stackFoodComponent;
    }

    @Override
    public boolean isItemStackFood() {
        return this.stackFoodComponent != null;
    }

    @Override
    public UseAction getFoodUseAction() {
        return this.useAction;
    }

    @Override
    public void setFoodUseAction(UseAction useAction) {
        if (useAction == UseAction.EAT || useAction == UseAction.DRINK) {
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
