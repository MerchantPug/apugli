package io.github.merchantpug.apugli.mixin;

import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageEnchantment.class)
public class DamageEnchantmentMixin extends Enchantment {
    @Shadow @Final @Mutable public int typeIndex;

    protected DamageEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    @Inject(method = "getAttackDamage", at = @At("HEAD"), cancellable = true)
    private void damageSmiteableTargets(int level, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        if (this.typeIndex == 1 && group == ApugliEntityGroups.SMITEABLE || this.typeIndex == 1 && group == ApugliEntityGroups.PLAYER_UNDEAD) {
            cir.setReturnValue ((float)level * 2.5F * 0.6F);
        }
    }
}
