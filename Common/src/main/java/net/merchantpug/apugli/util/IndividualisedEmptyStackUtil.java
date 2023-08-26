package net.merchantpug.apugli.util;

import net.fabricmc.loader.api.FabricLoader;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class IndividualisedEmptyStackUtil {

    private static final Map<Entity, ItemStack> ENTITY_EMPTY_STACK_MAP = new HashMap<>();

    public static ItemStack getEntityLinkedEmptyStack(Entity entity) {
        if (!ENTITY_EMPTY_STACK_MAP.containsKey(entity)) {
            ItemStack stack = new ItemStack((Void) null);
            Services.PLATFORM.setEntityToItemStack(stack, entity);
            ENTITY_EMPTY_STACK_MAP.put(entity, stack);
        }
        return ENTITY_EMPTY_STACK_MAP.get(entity);
    }

    public static void addEntityToStack(LivingEntity entity) {
        if (entity.isRemoved() && ENTITY_EMPTY_STACK_MAP.containsKey(entity)) {
            ENTITY_EMPTY_STACK_MAP.remove(entity);
            return;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);

            // Fix for Better Combat https://github.com/ZsoltMolnarrr/BetterCombat/issues/268.
            if (FabricLoader.getInstance().isModLoaded("bettercombat") && slot == EquipmentSlot.OFFHAND && entity instanceof Player player) {
                stack = player.getInventory().offhand.get(0);
            }

            if (Services.PLATFORM.getEntityFromItemStack(stack) == null) {
                if (stack == ItemStack.EMPTY) {
                    ItemStack newStack = getEntityLinkedEmptyStack(entity);
                    entity.setItemSlot(slot, newStack);
                } else
                    Services.PLATFORM.setEntityToItemStack(stack, entity);
            }
        }
    }

}
