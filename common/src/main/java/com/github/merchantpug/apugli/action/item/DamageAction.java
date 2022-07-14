/* MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.github.merchantpug.apugli.action.item;

import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.access.ItemStackAccess;
import com.github.merchantpug.apugli.powers.ActionOnDurabilityChange;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Random;
import java.util.function.Consumer;

public class DamageAction {
    public static void action(SerializableData.Instance data, ItemStack itemStack) {
        int amount = data.getInt("amount");
        int damage = amount;
        if (!itemStack.isDamageable()) return;
        if (amount > 0 && !data.getBoolean("ignore_unbreaking")) {
            int levels = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack);
            Random random = new Random();
            for (int i = 0; i < amount; i++) {
                if (UnbreakingEnchantment.shouldPreventDamage(itemStack, levels, random)) {
                    damage--;
                }
            }
            if (damage <= 0) {
                return;
            }
        }
        int newDamage = itemStack.getDamage() + damage;
        LivingEntity stackHolder = (LivingEntity)((ItemStackAccess)(Object)itemStack).getEntity();
        if (newDamage >= itemStack.getMaxDamage()) {
            if (stackHolder != null) {
                OriginComponent.getPowers(stackHolder, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(itemStack)).forEach(ActionOnDurabilityChange::executeBreakAction);
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
        } else {
            itemStack.setDamage(newDamage);
            if (stackHolder != null) {
                OriginComponent.getPowers(stackHolder, ActionOnDurabilityChange.class).stream().filter(p -> p.doesApply(itemStack)).forEach(ActionOnDurabilityChange::executeDecreaseAction);
            }
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