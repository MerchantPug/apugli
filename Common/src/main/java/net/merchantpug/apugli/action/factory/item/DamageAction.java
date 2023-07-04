package net.merchantpug.apugli.action.factory.item;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.function.Consumer;

public class DamageAction implements IActionFactory<Tuple<Level, Mutable<ItemStack>>> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("amount", SerializableDataTypes.INT, 1)
            .add("ignore_unbreaking", SerializableDataTypes.BOOLEAN, false);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Tuple<Level, Mutable<ItemStack>> levelItemStack) {
        ItemStack stack = levelItemStack.getB().getValue();
        if(!stack.isDamageableItem()) return;
        int amount = data.getInt("amount");
        int damage = amount;
        if(amount > 0 && !data.getBoolean("ignore_unbreaking")) {
            int levels = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
            for(int i = 0; i < amount; i++) {
                if(DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(stack, levels, levelItemStack.getA().random)) {
                    damage--;
                }
            }
            if(damage <= 0) {
                return;
            }
        }
        Entity entity = Services.PLATFORM.getItemStackLinkedEntity(stack);
        int newDamage = stack.getDamageValue() + damage;
        if(newDamage >= stack.getMaxDamage()) {
            if(entity instanceof LivingEntity holder) {
                EquipmentSlot equipmentSlot = null;
                for(EquipmentSlot slotValue : EquipmentSlot.values()) {
                    ItemStack slotStack = holder.getItemBySlot(slotValue);
                    if(slotStack == stack) {
                        equipmentSlot = slotValue;
                    }
                }

                if(equipmentSlot != null) {
                    EquipmentSlot finalEquipmentSlot = equipmentSlot;
                    Consumer<LivingEntity> breakCallback = l -> l.broadcastBreakEvent(finalEquipmentSlot);
                    breakCallback.accept(holder);
                }
            }
            stack.shrink(1);
            stack.setDamageValue(0);
        } else {
            stack.setDamageValue(newDamage);
        }
    }

}