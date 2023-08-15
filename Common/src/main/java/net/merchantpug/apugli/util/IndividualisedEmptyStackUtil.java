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
            ItemStack stack = new ItemStack((Item) null);
            Services.PLATFORM.setEntityToItemStack(stack, entity);
            ENTITY_EMPTY_STACK_MAP.put(entity, stack);
        }
        return ENTITY_EMPTY_STACK_MAP.get(entity);
    }

    public static void addEntityToStack(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (Services.PLATFORM.getEntityFromItemStack(stack) == null) {
                if (stack.isEmpty()) {
                    ItemStack newStack = getEntityLinkedEmptyStack(entity);
                    entity.setItemSlot(slot, newStack);
                }
                else
                    Services.PLATFORM.setEntityToItemStack(stack, entity);
            }
        }
    }

}
