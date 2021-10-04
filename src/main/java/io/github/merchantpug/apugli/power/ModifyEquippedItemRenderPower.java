package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ModifyEquippedItemRenderPower extends Power {
    public final EquipmentSlot slot;
    public final ItemStack stack;
    public final float scale;
    private final boolean renderEquipped;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyEquippedItemRenderPower>(Apugli.identifier("modify_equipped_item_render"),
                new SerializableData()
                        .add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
                        .add("stack", SerializableDataTypes.ITEM_STACK)
                        .add("scale", SerializableDataTypes.FLOAT, 1.0F)
                        .add("render_equipped", SerializableDataTypes.BOOLEAN, true),
                data ->
                        (type, entity) ->
                                new ModifyEquippedItemRenderPower(type, entity, (EquipmentSlot)data.get("equipment_slot"), (ItemStack)data.get("stack"), data.getFloat("scale"), data.getBoolean("render_equipped")))
                .allowCondition();
    }

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
