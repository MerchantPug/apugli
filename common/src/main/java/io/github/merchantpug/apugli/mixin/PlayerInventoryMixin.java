package io.github.merchantpug.apugli.mixin;

import io.github.apace100.origins.component.OriginComponent;
import io.github.merchantpug.apugli.powers.ActionOnEquipPower;
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
public abstract class PlayerInventoryMixin {
    @Shadow
    @Final
    public DefaultedList<ItemStack> armor;

    @Shadow @Final public DefaultedList<ItemStack> offHand;

    @Shadow @Final public PlayerEntity player;

    @Shadow public abstract ItemStack getCursorStack();

    @Redirect(method = "updateItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    private boolean isEmpty(ItemStack self) {
        return false;
    }

    @Inject(method = "setStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setStack(int slot, ItemStack stack, CallbackInfo ci, DefaultedList<ItemStack> defaultedList) {
        ItemStack currentItem = defaultedList.get(slot);

        if (currentItem.getItem().equals(stack.getItem()) && this.getCursorStack().isEmpty()) return;

        if (defaultedList.equals(this.armor)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, slot);
            OriginComponent.getPowers(this.player, ActionOnEquipPower.class).forEach(power -> power.fireAction(equipmentSlot, stack));
        }

        if (defaultedList.equals(this.offHand)) {
            EquipmentSlot equipmentSlot = EquipmentSlot.OFFHAND;
            OriginComponent.getPowers(this.player, ActionOnEquipPower.class).forEach(power -> power.fireAction(equipmentSlot, stack));
        }
    }
}