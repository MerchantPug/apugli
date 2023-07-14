package net.merchantpug.apugli.util;

import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class IndividualisedEmptyStackUtil {

    private static final Map<Entity, ItemStack> ENTITY_EMPTY_STACK_MAP = new HashMap<>();

    public static ItemStack getEntityLinkedEmptyStack(Entity entity) {
        if (!ENTITY_EMPTY_STACK_MAP.containsKey(entity)) {
            ENTITY_EMPTY_STACK_MAP.put(entity, new ItemStack((Item) null));
        }
        return ENTITY_EMPTY_STACK_MAP.get(entity);
    }

    public static void addEntityToStack(LivingEntity entity) {
        for (ItemStack stack : entity.getAllSlots()) {
            if (Services.PLATFORM.getEntityFromItemStack(stack) == null) {
                ItemStack iteratedStack = stack.isEmpty() ? getEntityLinkedEmptyStack(entity) : stack;
                Services.PLATFORM.setEntityToItemStack(iteratedStack, entity);
                if (stack.isEmpty()) {
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        if (ItemStack.matches(iteratedStack, entity.getItemBySlot(slot))) {
                            entity.setItemSlot(slot, iteratedStack);
                        }
                    }
                }
            }
        }
    }

}
