package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ModifyEquippedItemRenderPower extends Power {
    public final EquipmentSlot slot;
    public final ItemStack stack;
    public final float scale;
    private final boolean override;
    private final boolean mergeWithHeld;
    public final BipedEntityModel.ArmPose armPose;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyEquippedItemRenderPower>(Apugli.identifier("modify_equipped_item_render"),
                new SerializableData()
                        .add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
                        .add("stack", SerializableDataTypes.ITEM_STACK)
                        .add("scale", SerializableDataTypes.FLOAT, 1.0F)
                        .add("override", SerializableDataTypes.BOOLEAN, false)
                        .add("merge_with_held", SerializableDataTypes.BOOLEAN, false)
                        .add("arm_pose", ApugliDataTypes.ARM_POSE, null),
                data ->
                        (type, entity) ->
                                new ModifyEquippedItemRenderPower(type, entity, (EquipmentSlot)data.get("equipment_slot"), (ItemStack)data.get("stack"), data.getFloat("scale"), data.getBoolean("override"), data.getBoolean("merge_with_held"), (BipedEntityModel.ArmPose)data.get("arm_pose")))
                .allowCondition();
    }

    public ModifyEquippedItemRenderPower(PowerType<?> type, LivingEntity entity, EquipmentSlot slot, ItemStack stack, float scale, boolean override, boolean mergeWithHeld, BipedEntityModel.ArmPose armPose) {
        super(type, entity);
        this.slot = slot;
        this.stack = stack;
        this.scale = scale;
        this.override = override;
        this.mergeWithHeld = mergeWithHeld;
        this.armPose = armPose;
    }

    public boolean isSlotForArmor() {
        return this.slot == EquipmentSlot.HEAD || this.slot == EquipmentSlot.CHEST || this.slot == EquipmentSlot.LEGS || this.slot == EquipmentSlot.FEET;
    }

    public boolean shouldOverride() {
        return this.override;
    }

    public boolean shouldMergeWithHeld() {
        return this.mergeWithHeld;
    }
}
