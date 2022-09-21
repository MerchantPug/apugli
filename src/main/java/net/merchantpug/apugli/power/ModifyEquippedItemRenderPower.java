package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ModifyEquippedItemRenderPower extends Power {
    public final EquipmentSlot slot;
    public final ItemStack stack;
    public final float scale;
    private final boolean override;
    private final boolean mergeWithHeld;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyEquippedItemRenderPower>(Apugli.identifier("modify_equipped_item_render"),
                new SerializableData()
                        .add("slot", SerializableDataTypes.EQUIPMENT_SLOT)
                        .add("stack", SerializableDataTypes.ITEM_STACK)
                        .add("scale", SerializableDataTypes.FLOAT, 1.0F)
                        .add("override", SerializableDataTypes.BOOLEAN, false)
                        .add("merge_with_held", SerializableDataTypes.BOOLEAN, false),
                data ->
                        (type, entity) ->
                                new ModifyEquippedItemRenderPower(type, entity, (EquipmentSlot)data.get("slot"), (ItemStack)data.get("stack"), data.getFloat("scale"), data.getBoolean("override"), data.getBoolean("merge_with_held")))
                .allowCondition();
    }

    public ModifyEquippedItemRenderPower(PowerType<?> type, LivingEntity entity, EquipmentSlot slot, ItemStack stack, float scale, boolean override, boolean mergeWithHeld) {
        super(type, entity);
        this.slot = slot;
        this.stack = stack;
        this.scale = scale;
        this.override = override;
        this.mergeWithHeld = mergeWithHeld;
    }

    public boolean shouldOverride() {
        return this.override;
    }

    public boolean shouldMergeWithHeld() {
        return this.mergeWithHeld;
    }
}
