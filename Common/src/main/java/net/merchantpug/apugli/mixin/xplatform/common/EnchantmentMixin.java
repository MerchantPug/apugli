package net.merchantpug.apugli.mixin.xplatform.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Unique
    private ItemStack apugli$capturedItem;

    @Inject(method = "getSlotItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void apugli$captureSlotItem(LivingEntity pEntity, CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, Map map, EquipmentSlot[] var3, int var4, int var5, EquipmentSlot equipmentslot, ItemStack stack) {
        this.apugli$capturedItem = stack;
    }

    @ModifyExpressionValue(method = "getSlotItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private boolean apugli$allowEmptySlotItemIfModified(boolean original) {
        if (this.apugli$capturedItem != null && this.apugli$capturedItem.isEmpty() && Services.PLATFORM.getEntityFromItemStack(apugli$capturedItem) instanceof LivingEntity living && ApugliPowers.MODIFY_ENCHANTMENT_LEVEL.get().getEntityItemEnchants().containsKey(living.getUUID())) {
            original = false;
        }
        this.apugli$capturedItem = null;
        return original;
    }

}
