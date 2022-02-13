package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.ActionOnDurabilityChange;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {
    @Inject(method = "repairPlayerGears", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void executeActionOnDurabilityIncrease(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir, Map.Entry<EquipmentSlot, ItemStack> entry) {
        PowerHolderComponent.getPowers(player, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(entry.getValue())).forEach(ActionOnDurabilityChange::executeIncreaseAction);
    }
}
