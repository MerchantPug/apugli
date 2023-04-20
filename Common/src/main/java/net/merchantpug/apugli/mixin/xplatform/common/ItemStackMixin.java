package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.access.ItemStackAccess;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.BucketItemAccessor;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.ItemAccessor;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    private void cacheEntity(Level world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (this.apugli$getEntity() == null) this.apugli$setEntity(entity);
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setPopTime(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void copyNewParams(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        if (this.apugli$getEntity() != null) {
            ((ItemStackAccess) (Object) itemStack).setEntity(this.apugli$getEntity());
        }
    }

    public void apugli$setEntity(Entity entity) { this.apugli$entity = entity; }

    public Entity apugli$getEntity() {
        return this.apugli$entity;
    }

    @Unique
    private FoodProperties apugli$stackFoodComponent;

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getDamageValue()I", ordinal = 1))
    private void executeActionOnDurabilityDecrease(int amount, RandomSource random, ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(player, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeDecreaseAction);
    }

    @Inject(method = "hurtAndBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private <T extends LivingEntity> void executeActionBroken(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
        PowerHolderComponent.getPowers(entity, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeBreakAction);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (user.canConsume(power.get().foodComponent.isAlwaysEdible())) {
                user.setCurrentHand(hand);
                if (this.getItem() instanceof BucketItem) {
                    BlockHitResult blockHitResult = ItemAccessor.callRaycast(world, user, ((BucketItemAccessor)this.getItem()).getFluid() == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) return;
                }
                cir.setReturnValue(InteractionResultHolder.consume(itemStack));
            }
        }
    }

    @Inject(method = "finishUsingItem", at = @At("RETURN"), cancellable = true)
    private void finishUsing(Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            ItemStack newStack = this.copy();
            ((ItemStackAccess)(Object)newStack).setItemStackFoodComponent(power.get().foodComponent);
            newStack = user.eat(world, newStack);
            if (user instanceof Player player && !player.getAbilities().instabuild) {
                if (power.get().returnStack != null && newStack.isEmpty()) {
                    cir.setReturnValue(power.get().returnStack.copy());
                } else {
                    if (power.get().returnStack != null) {
                        ItemStack stack2 = power.get().returnStack.copy();
                        if (!player.addItem(stack2)) {
                            player.drop(stack2, false);
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

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void getUseAction(CallbackInfoReturnable<UseAnim> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.useAction != null).findFirst();
        if (power.isPresent()) {
            this.apugli$setItemStackFoodComponent(power.get().foodComponent);
            cir.setReturnValue(power.get().useAction.equals(UseAction.DRINK) ? power.get().useAction : UseAction.EAT);
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void getMaxUseTime(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.foodComponent.isSnack() ? 16 : 32));
    }

    @Inject(method = "getDrinkingSound", at = @At("HEAD"), cancellable = true)
    private void getDrinkSound(CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.sound != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.sound));
    }

    @Inject(method = "getEatingSound", at = @At("HEAD"), cancellable = true)
    private void getEatSound(CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.sound != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.sound));
    }

    public FoodProperties apugli$getItemStackFoodComponent() {
        return this.apugli$stackFoodComponent;
    }

    public void apugli$setItemStackFoodComponent(FoodProperties value) {
        this.apugli$stackFoodComponent = value;
    }
}