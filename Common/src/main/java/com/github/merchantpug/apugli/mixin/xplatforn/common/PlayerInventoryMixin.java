<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/PlayerInventoryMixin.java
package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.power.ActionOnEquipPower;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common;

import the.great.migration.merchantpug.apugli.power.ActionOnEquipPower;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/PlayerInventoryMixin.java
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
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

    @Shadow @Final public NonNullList<ItemStack> offHand;

    @Shadow @Final public Player player;

    @Redirect(method = "updateItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean isEmpty(ItemStack self) {
        return false;
    }

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/PlayerInventoryMixin.java
    @Inject(method = "setStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void setStack(int slot, ItemStack stack, CallbackInfo ci, DefaultedList<ItemStack> defaultedList) {
        ItemStack currentItem = defaultedList.get(slot);

        if (this.player == null || currentItem.getItem().equals(stack.getItem()) && this.player.currentScreenHandler.getCursorStack().isEmpty()) return;
========
    @Inject(method = "setStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setStack(int slot, ItemStack stack, CallbackInfo ci, NonNullList<ItemStack> defaultedList) {
        ItemStack currentItem = defaultedList.get(slot);

        if(currentItem.getItem().equals(stack.getItem()) && this.player.containerMenu.getCarried().isEmpty()) return;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/PlayerInventoryMixin.java

        if(defaultedList.equals(this.armor)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, slot);
            PowerHolderComponent.getPowers(this.player, ActionOnEquipPower.class).forEach(power -> power.fireAction(equipmentSlot, stack));
        }

        if(defaultedList.equals(this.offHand)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.OFFHAND;
            PowerHolderComponent.getPowers(this.player, ActionOnEquipPower.class).forEach(power -> power.fireAction(equipmentSlot, stack));
        }
    }
}
