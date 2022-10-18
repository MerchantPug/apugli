package com.github.merchantpug.apugli.mixin;

import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.power.ActionOnEquipPower;
import com.github.merchantpug.apugli.registry.ExamplePowerFactories;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Shadow @Final public Player player;

    @Shadow @Final public NonNullList<ItemStack> armor;

    @Shadow @Final public NonNullList<ItemStack> offhand;

    @Inject(method = "setItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setStack(int slot, ItemStack stack, CallbackInfo ci, NonNullList<ItemStack> defaultedList) {
        ItemStack currentItem = defaultedList.get(slot);

        if(currentItem.getItem().equals(stack.getItem()) && this.player.containerMenu.getCarried().isEmpty()) return;

        if(defaultedList.equals(this.armor)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, slot);
            Services.PLATFORM.getPowers(this.player, ActionOnEquipPower.class, ExamplePowerFactories.ACTION_ON_EQUIP).forEach(power -> power.fireAction(equipmentSlot, stack));
        }

        if(defaultedList.equals(this.offhand)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.OFFHAND;
            Services.PLATFORM.getPowers(this.player, ActionOnEquipPower.class, ExamplePowerFactories.ACTION_ON_EQUIP).forEach(power -> power.fireAction(equipmentSlot, stack));
        }
    }
}