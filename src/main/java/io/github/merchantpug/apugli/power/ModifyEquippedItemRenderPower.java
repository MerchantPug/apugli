package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ModifyEquippedItemRenderPower extends Power {
    public final EquipmentSlot slot;
    public final ItemStack stack;
    public final float scale;
    private final boolean renderEquipped;

    public ModifyEquippedItemRenderPower(PowerType<?> type, LivingEntity entity, EquipmentSlot slot, ItemStack stack, float scale, boolean renderEquipped) {
        super(type, entity);
        this.slot = slot;
        this.stack = stack;
        this.scale = scale;
        this.renderEquipped = renderEquipped;
    }

    public boolean isSlotForArmor() {
        return this.slot == EquipmentSlot.HEAD || this.slot == EquipmentSlot.CHEST || this.slot == EquipmentSlot.LEGS || this.slot == EquipmentSlot.FEET;
    }

    public boolean shouldRenderEquipped() {
        return this.renderEquipped;
    }
}
