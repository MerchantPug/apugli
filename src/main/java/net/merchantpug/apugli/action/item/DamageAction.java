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

package net.merchantpug.apugli.action.item;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ItemStackAccess;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.power.ActionOnDurabilityChangePower;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class DamageAction {
    public static void action(SerializableData.Instance data, Pair<World, ItemStack> worldAndStack) {
        if (!worldAndStack.getRight().isDamageable()) return;
        int amount = data.getInt("amount");
        int damage = amount;
        if (amount > 0 && !data.getBoolean("ignore_unbreaking")) {
            int levels = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, worldAndStack.getRight());
            for (int i = 0; i < amount; i++) {
                if (UnbreakingEnchantment.shouldPreventDamage(worldAndStack.getRight(), levels, worldAndStack.getLeft().random)) {
                    damage--;
                }
            }
            if (damage <= 0) {
                return;
            }
        }
        LivingEntity stackHolder = (LivingEntity) ((ItemStackAccess) (Object) worldAndStack.getRight()).getEntity();
        int newDamage = worldAndStack.getRight().getDamage() + damage;
        if (newDamage >= worldAndStack.getRight().getMaxDamage()) {
            if (stackHolder != null) {
                PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChangePower.class).stream().filter(p -> p.doesApply(worldAndStack.getRight())).forEach(ActionOnDurabilityChangePower::executeBreakAction);
                EquipmentSlot equipmentSlot = null;
                for (EquipmentSlot slotValue : EquipmentSlot.values()) {
                    ItemStack stack = stackHolder.getEquippedStack(slotValue);
                    if (stack == worldAndStack.getRight()) {
                        equipmentSlot = slotValue;
                    }
                }

                if (equipmentSlot != null) {
                    EquipmentSlot finalEquipmentSlot = equipmentSlot;
                    Consumer<LivingEntity> breakCallback = entity -> entity.sendEquipmentBreakStatus(finalEquipmentSlot);
                    breakCallback.accept(stackHolder);
                }
            }
            worldAndStack.getRight().decrement(1);
            worldAndStack.getRight().setDamage(0);
        } else {
            worldAndStack.getRight().setDamage(newDamage);
            if (stackHolder != null) {
                if (amount < 0) {
                    PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChangePower.class).stream().filter(p -> p.doesApply(worldAndStack.getRight())).forEach(ActionOnDurabilityChangePower::executeIncreaseAction);
                } else {
                    PowerHolderComponent.getPowers(stackHolder, ActionOnDurabilityChangePower.class).stream().filter(p -> p.doesApply(worldAndStack.getRight())).forEach(ActionOnDurabilityChangePower::executeDecreaseAction);
                }
            }
        }
    }

    public static ActionFactory<Pair<World, ItemStack>> getFactory() {
        return new ActionFactory<>(Apugli.identifier("damage"),
                new SerializableData()
                    .add("amount", SerializableDataTypes.INT, 1)
                    .add("ignore_unbreaking", SerializableDataTypes.BOOLEAN, false),
                DamageAction::action
        );
    }
}