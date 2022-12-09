package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.power.ActionOnDurabilityChange;
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
    private void cacheEntity(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (this.apugli$getEntity() == null) this.apugli$setEntity(entity);
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setBobbingAnimationTime(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
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
    private FoodComponent apugli$stackFoodComponent;

    @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getDamage()I", ordinal = 1))
    private void executeActionOnDurabilityDecrease(int amount, net.minecraft.util.math.random.Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        PowerHolderComponent.getPowers(player, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeDecreaseAction);
    }

    @Inject(method = "damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private <T extends LivingEntity> void executeActionBroken(int amount, T entity, Consumer<T> breakCallback, CallbackInfo ci) {
        PowerHolderComponent.getPowers(entity, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(ActionOnDurabilityChange::executeBreakAction);
    }

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
                }
                cir.setReturnValue(TypedActionResult.consume(itemStack));
            }
        }
    }

    @Inject(method = "finishUsing", at = @At("RETURN"), cancellable = true)
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
    private void getUseAction(CallbackInfoReturnable<UseAction> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.useAction != null).findFirst();
        if (power.isPresent()) {
            this.apugli$setItemStackFoodComponent(power.get().foodComponent);
            cir.setReturnValue(power.get().useAction.equals(UseAction.DRINK) ? power.get().useAction : UseAction.EAT);
        }
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void getMaxUseTime(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.foodComponent.isSnack() ? 16 : 32));
    }

    @Inject(method = "getDrinkSound", at = @At("HEAD"), cancellable = true)
    private void getDrinkSound(CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.sound != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.sound));
    }

    @Inject(method = "getEatSound", at = @At("HEAD"), cancellable = true)
    private void getEatSound(CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(this.apugli$getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.sound != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.sound));
    }

    public FoodComponent apugli$getItemStackFoodComponent() {
        return this.apugli$stackFoodComponent;
    }

    public void apugli$setItemStackFoodComponent(FoodComponent value) {
        this.apugli$stackFoodComponent = value;
    }
}
