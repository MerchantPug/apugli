package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Inventory.class)
public class PlayerInventoryMixin {
    @Shadow @Final public NonNullList<ItemStack> armor;

    @Shadow @Final public NonNullList<ItemStack> offhand;

    @Shadow @Final public Player player;

    @Inject(method = "setItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void apugli$setStack(int slot, ItemStack stack, CallbackInfo ci, NonNullList<ItemStack> defaultedList) {
        ItemStack currentItem = defaultedList.get(slot);

        if(this.player == null || currentItem.getItem().equals(stack.getItem()) && this.player.containerMenu.getCarried().isEmpty()) return;

        if(defaultedList.equals(this.armor)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, slot);
            Services.POWER.getPowers(this.player, ApugliPowers.ACTION_ON_EQUIP.get()).forEach(power -> power.executeAction(equipmentSlot, stack));
        }

        if(defaultedList.equals(this.offhand)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.OFFHAND;
            Services.POWER.getPowers(this.player, ApugliPowers.ACTION_ON_EQUIP.get()).forEach(power -> power.executeAction(equipmentSlot, stack));
        }
    }
}
