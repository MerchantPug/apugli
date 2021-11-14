package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.access.ItemStackAccess;
import io.github.merchantpug.apugli.power.EdibleItemPower;
import io.github.merchantpug.apugli.power.ModifyEnchantmentLevelPower;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackAccess {
    @Unique
    public Entity entity;

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void cacheEntity(World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (this.getEntity() == null) this.setEntity(entity);
    }

    @Inject(method = "copy", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCooldown(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void copyEntity(CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack) {
        if (this.getEntity() != null) {
            ((ItemStackAccess) (Object) itemStack).setEntity(this.getEntity());
        }
    }

    @Inject(method = "addEnchantment", at = @At(value = "TAIL"))
    private void addEnchantment(Enchantment enchantment, int level, CallbackInfo ci) {
        ModifyEnchantmentLevelPower.updateEnchantments((ItemStack) (Object) this);
    }

    public void setEntity(Entity entity) { this.entity = entity; }

    public Entity getEntity() {
        return this.entity;
    }

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void executeEntityActions(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if(user != null) {
            PowerHolderComponent.KEY.get(user).getPowers(EdibleItemPower.class).stream().filter(p -> p.doesApply((ItemStack)(Object)this)).forEach(EdibleItemPower::eat);
        }
    }
}
