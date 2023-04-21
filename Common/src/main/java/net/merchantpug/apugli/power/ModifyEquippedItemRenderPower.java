package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ModifyEquippedItemRenderPower extends Power {
    private final EquipmentSlot slot;
    private final ItemStack stack;
    private final float scale;
    private final boolean override;
    private final boolean mergeWithHeld;

    public ModifyEquippedItemRenderPower(PowerType<?> type, LivingEntity entity, EquipmentSlot slot, ItemStack stack, float scale, boolean override, boolean mergeWithHeld) {
        super(type, entity);
        this.slot = slot;
        this.stack = stack;
        this.scale = scale;
        this.override = override;
        this.mergeWithHeld = mergeWithHeld;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public ItemStack getStack() {
        return stack;
    }

    public float getScale() {
        return scale;
    }

    public boolean shouldOverride() {
        return this.override;
    }

    public boolean shouldMergeWithHeld() {
        return this.mergeWithHeld;
    }

    public static class Factory extends SimplePowerFactory<ModifyEquippedItemRenderPower> {

        public Factory() {
            super("modify_equipped_item_render",
                    new SerializableData()
                            .add("slot", SerializableDataTypes.EQUIPMENT_SLOT)
                            .add("stack", SerializableDataTypes.ITEM_STACK)
                            .add("scale", SerializableDataTypes.FLOAT, 1.0F)
                            .add("override", SerializableDataTypes.BOOLEAN, false)
                            .add("merge_with_held", SerializableDataTypes.BOOLEAN, false),
                    data -> (type, entity) -> new ModifyEquippedItemRenderPower(type, entity, data.get("slot"), data.get("stack"), data.getFloat("scale"), data.getBoolean("override"), data.getBoolean("merge_with_held")));
            allowCondition();
        }

        @Override
        public Class<ModifyEquippedItemRenderPower> getPowerClass() {
            return ModifyEquippedItemRenderPower.class;
        }

    }

}
