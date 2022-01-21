package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
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
                        .add("equipment_slot", SerializableDataType.EQUIPMENT_SLOT)
                        .add("stack", SerializableDataType.ITEM_STACK)
                        .add("scale", SerializableDataType.FLOAT, 1.0F)
                        .add("override", SerializableDataType.BOOLEAN, false)
                        .add("merge_with_held", SerializableDataType.BOOLEAN, false),
                data ->
                        (type, player) ->
                                new ModifyEquippedItemRenderPower(type, player, (EquipmentSlot)data.get("equipment_slot"), (ItemStack)data.get("stack"), data.getFloat("scale"), data.getBoolean("override"), data.getBoolean("merge_with_held")))
                .allowCondition();
    }

    public ModifyEquippedItemRenderPower(PowerType<?> type, PlayerEntity player, EquipmentSlot slot, ItemStack stack, float scale, boolean override, boolean mergeWithHeld) {
        super(type, player);
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