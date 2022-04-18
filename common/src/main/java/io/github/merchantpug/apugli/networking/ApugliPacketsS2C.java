package io.github.merchantpug.apugli.networking;

import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.ApugliClient;
import io.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import io.github.merchantpug.apugli.util.StackFoodComponentUtil;
import me.shedaniel.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;

public class ApugliPacketsS2C {
    @Environment(EnvType.CLIENT)
    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), ApugliPackets.REMOVE_STACK_FOOD_COMPONENT, ApugliPacketsS2C::onFoodComponentSync);
        NetworkManager.registerReceiver(NetworkManager.s2c(), ApugliPackets.REMOVE_KEYS_TO_CHECK, ApugliPacketsS2C::onRemoveKeysToCheck);
    }

    private static void onRemoveKeysToCheck(PacketByteBuf packetByteBuf, NetworkManager.PacketContext context) {
        context.queue(() -> {
            ApugliClient.keysToCheck.clear();
        });
    }
    private static void onFoodComponentSync(PacketByteBuf packetByteBuf, NetworkManager.PacketContext context) {
        int targetId = packetByteBuf.readInt();

        boolean usesEquipmentSlot = packetByteBuf.readBoolean();
        String equipmentSlotId = "";
        if (usesEquipmentSlot) {
            equipmentSlotId = packetByteBuf.readString(Short.MAX_VALUE);
        }
        String finalEquipmentSlotId = equipmentSlotId;

        boolean usesInventoryIndex = packetByteBuf.readBoolean();
        StackFoodComponentUtil.InventoryLocation inventoryLocation = null;
        int inventoryIndex = 0;
        if (usesInventoryIndex) {
            inventoryLocation = StackFoodComponentUtil.InventoryLocation.values()[packetByteBuf.readByte()];
            inventoryIndex = packetByteBuf.readInt();
        }
        StackFoodComponentUtil.InventoryLocation finalInventoryLocation = inventoryLocation;
        int finalInventoryIndex = inventoryIndex;

        context.queue(() -> {
            Entity entity = context.getPlayer().getEntityWorld().getEntityById(targetId);
            if (!(entity instanceof PlayerEntity)) {
                Apugli.LOGGER.warn("Received unknown target");
            }  else {
                if (usesEquipmentSlot) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.byName(finalEquipmentSlotId);
                    ItemStack stack = ((PlayerEntity)entity).getEquippedStack(equipmentSlot);
                    ItemStackFoodComponentUtil.removeStackFood(stack);
                }
                if (usesInventoryIndex) {
                    DefaultedList<ItemStack> inventory;
                    switch(finalInventoryLocation) {
                        case MAIN:
                            inventory = ((PlayerEntity) entity).inventory.main;
                            break;
                        case ARMOR:
                            inventory = ((PlayerEntity) entity).inventory.armor;
                            break;
                        case OFFHAND:
                            inventory = ((PlayerEntity) entity).inventory.offHand;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + finalInventoryLocation);
                    }
                    ItemStackFoodComponentUtil.removeStackFood(inventory.get(finalInventoryIndex));
                }
            }
        });
    }
}
