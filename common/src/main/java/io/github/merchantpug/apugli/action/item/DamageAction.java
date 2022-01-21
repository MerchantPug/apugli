package io.github.merchantpug.apugli.action.item;

import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.ItemStackAccess;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.EnumMap;
import java.util.Random;
import java.util.function.Consumer;

public class DamageAction {
    public static void action(SerializableData.Instance data, ItemStack itemStack) {
        int amount = data.getInt("amount");
        int damage = amount;
        if (amount > 0 && !data.getBoolean("ignore_unbreaking")) {
            int levels = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack);
            Random random = new Random();
            for (int i = 0; i < amount; i++) {
                if (UnbreakingEnchantment.shouldPreventDamage(itemStack, levels, random)) {
                    damage--;
                }
            }
        }
        int newDamage = itemStack.getDamage() + damage;
        itemStack.setDamage(newDamage);
        if (newDamage >= itemStack.getMaxDamage()) {
            LivingEntity stackHolder = (LivingEntity)((ItemStackAccess)(Object)itemStack).getEntity();

            if (stackHolder != null) {
                EquipmentSlot equipmentSlot = null;
                for (EquipmentSlot slotValue : EquipmentSlot.values()) {
                    ItemStack stack = stackHolder.getEquippedStack(slotValue);
                    if (stack == itemStack) {
                        equipmentSlot = slotValue;
                    }
                }

                if (equipmentSlot != null) {
                    EquipmentSlot finalEquipmentSlot = equipmentSlot;
                    Consumer<LivingEntity> breakCallback = entity -> entity.sendEquipmentBreakStatus(finalEquipmentSlot);
                    breakCallback.accept(stackHolder);
                }
            }

            itemStack.decrement(1);
            itemStack.setDamage(0);
        }
    }

    public static ActionFactory<ItemStack> getFactory() {
        return new ActionFactory<>(Apugli.identifier("damage"),
                new SerializableData()
                    .add("amount", SerializableDataType.INT, 1)
                    .add("ignore_unbreaking", SerializableDataType.BOOLEAN, false),
                DamageAction::action
        );
    }
}