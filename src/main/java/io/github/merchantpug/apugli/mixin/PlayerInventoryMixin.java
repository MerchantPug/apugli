package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.ActionOnEquipPower;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Shadow @Final public DefaultedList<ItemStack> armor;

    @Shadow @Final public DefaultedList<ItemStack> offHand;

    @Shadow @Final public PlayerEntity player;

    @Redirect(method = "updateItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean isEmpty(ItemStack self) {
        return false;
    }

    @Inject(method = "setStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setStack(int slot, ItemStack stack, CallbackInfo ci, DefaultedList<ItemStack> defaultedList) {
        ItemStack currentItem = defaultedList.get(slot);

        if (currentItem.getItem().equals(stack.getItem()) && this.player.currentScreenHandler.getCursorStack().isEmpty()) return;

        if (defaultedList.equals(this.armor)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, slot);
            PowerHolderComponent.getPowers(this.player, ActionOnEquipPower.class).forEach(power -> power.fireAction(equipmentSlot, stack));
        }

        if (defaultedList.equals(this.offHand)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.OFFHAND;
            PowerHolderComponent.getPowers(this.player, ActionOnEquipPower.class).forEach(power -> power.fireAction(equipmentSlot, stack));
        }
    }
}
