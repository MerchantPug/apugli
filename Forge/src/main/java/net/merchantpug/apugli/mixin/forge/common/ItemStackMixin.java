package net.merchantpug.apugli.mixin.forge.common;

import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.access.ItemStackLevelAccess;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.ItemAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.EdibleItemPower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.merchantpug.apugli.util.CoreUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
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
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemStack.class)
@Implements(@Interface(iface = ItemStackLevelAccess.class, prefix = "apugli$"))
public abstract class ItemStackMixin {
    @Shadow public abstract Item getItem();

    public Level apugli$level;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void getLevelFromInventory(Level level, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        apugli$level = level;
    }

    @Inject(method = "isEdible", at = @At(value = "RETURN"), cancellable = true)
    private void setEdibleWithPower(CallbackInfoReturnable<Boolean> cir) {
        if (((ItemStackAccess)this).getEntity() instanceof LivingEntity living && CoreUtil.doEdibleItemPowersApply((ItemStack)(Object)this, living))
            cir.setReturnValue(true);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (!(this.getItem() instanceof BucketItem bucket) || !(((ItemStackAccess)(Object)stack).getEntity() instanceof LivingEntity living)) return;
        Optional<EdibleItemPower> power = Services.POWER.getPowers(living, ApugliPowers.EDIBLE_ITEM.get()).stream().filter(p -> p.doesApply(stack)).findFirst();
        if (power.isPresent()) {
            ItemStack itemStack = user.getItemInHand(hand);
            if (user.canEat(power.get().getFoodComponent().canAlwaysEat())) {
                user.startUsingItem(hand);
                if (this.getItem() instanceof BucketItem) {
                    BlockHitResult blockHitResult = ItemAccessor.callRaycast(world, user, bucket.getFluid() == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) return;
                }
                cir.setReturnValue(InteractionResultHolder.consume(itemStack));
            }
        }

    }

    public Level apugli$getLevel() {
        return apugli$level;
    }

    public void apugli$setLevel(Level value) {
        apugli$level = value;
    }

}
