package net.merchantpug.apugli.mixin.fabric.common;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.mixin.fabric.common.accessor.BucketItemAccessor;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.ItemAccessor;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract ItemStack copy();

    @Shadow public abstract Item getItem();

    @Unique
    private FoodProperties apugli$stackFoodComponent;

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(((ItemStackAccess)(Object)stack).getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            ItemStack itemStack = user.getItemInHand(hand);
            if (user.canEat(power.get().foodComponent.isFastFood())) {
                user.startUsingItem(hand);
                if (this.getItem() instanceof BucketItem) {
                    BlockHitResult blockHitResult = ItemAccessor.callRaycast(world, user, ((BucketItemAccessor)this.getItem()).getContent() == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) return;
                }
                cir.setReturnValue(InteractionResultHolder.consume(itemStack));
            }
        }
    }

    @Inject(method = "finishUsingItem", at = @At("RETURN"), cancellable = true)
    private void finishUsing(Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(((ItemStackAccess)(Object)stack).getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
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
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(((ItemStackAccess)(Object)stack).getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.useAction != null).findFirst();
        if (power.isPresent()) {
            this.apugli$setItemStackFoodComponent(power.get().foodComponent);
            cir.setReturnValue(power.get().useAction.equals(UseAnim.DRINK) ? power.get().useAction : UseAnim.EAT);
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void getMaxUseTime(CallbackInfoReturnable<Integer> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(((ItemStackAccess)(Object)stack).getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.foodComponent.isFastFood() ? 16 : 32));
    }

    @Inject(method = "getDrinkingSound", at = @At("HEAD"), cancellable = true)
    private void getDrinkSound(CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(((ItemStackAccess)(Object)stack).getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.sound != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.sound));
    }

    @Inject(method = "getEatingSound", at = @At("HEAD"), cancellable = true)
    private void getEatSound(CallbackInfoReturnable<SoundEvent> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(((ItemStackAccess)(Object)stack).getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack) && p.sound != null).findFirst();
        power.ifPresent(edibleItemPower -> cir.setReturnValue(edibleItemPower.sound));
    }

    public FoodProperties apugli$getItemStackFoodComponent() {
        return this.apugli$stackFoodComponent;
    }

    public void apugli$setItemStackFoodComponent(FoodProperties value) {
        this.apugli$stackFoodComponent = value;
    }

}
