package net.merchantpug.apugli.mixin.fabric.common;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.mixin.fabric.common.accessor.BucketItemAccessor;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.ItemAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow public abstract ItemStack copy();

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        Optional<EdibleItemPower> power = PowerHolderComponent.getPowers(((ItemStackAccess)(Object)stack).getEntity(), EdibleItemPower.class).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            ItemStack itemStack = user.getItemInHand(hand);
            if (user.canEat(power.get().getFoodComponent().canAlwaysEat())) {
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
        if (!(((ItemStackAccess)(Object)stack).getEntity() instanceof LivingEntity living)) return;
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            EdibleItemPower.executeEntityActions(user, stack);
            ItemStack newStack = this.copy();
            newStack = user.eat(world, newStack);
            if (!(user instanceof Player player) || !player.getAbilities().instabuild) {
                newStack.shrink(1);
            }
            if (user instanceof Player player && !player.getAbilities().instabuild) {
                if (power.get().getReturnStack() != null) {
                    ItemStack returnStack = power.get().getReturnStack().copy();
                    if (newStack.isEmpty()) {
                        cir.setReturnValue(EdibleItemPower.executeItemActions(user, returnStack, stack));
                    } else {
                        ItemStack stack2 = EdibleItemPower.executeItemActions(user, returnStack, stack);
                        if (!player.addItem(stack2)) {
                            player.drop(stack2, false);
                        }
                    }
                } else {
                    cir.setReturnValue(EdibleItemPower.executeItemActions(user, newStack, stack));
                }
            }
        }
    }

}
