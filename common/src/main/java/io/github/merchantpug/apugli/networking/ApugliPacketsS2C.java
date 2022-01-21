package io.github.merchantpug.apugli.networking;

import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.BackportedDataTypes;
import io.github.merchantpug.apugli.util.ItemStackFoodComponentAPI;
import io.github.merchantpug.apugli.util.StackFoodComponentUtil;
import me.shedaniel.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class ApugliPacketsS2C {
    @Environment(EnvType.CLIENT)
    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), ApugliPackets.SYNC_STACK_FOOD_COMPONENT, ApugliPacketsS2C::onFoodComponentSync);
    }

    private static void onFoodComponentSync(PacketByteBuf packetByteBuf, NetworkManager.PacketContext context) {
        int targetId = packetByteBuf.readInt();
        StackFoodComponentUtil.FoodComponentAction foodComponentAction = StackFoodComponentUtil.FoodComponentAction.values()[packetByteBuf.readByte()];
        FoodComponent foodComponent = null;
        boolean hasUseAction;
        UseAction useAction = null;
        boolean hasReturnStack;
        ItemStack returnStack = null;
        boolean hasSoundEvent;
        Identifier soundEventId = null;
        if (foodComponentAction == StackFoodComponentUtil.FoodComponentAction.ADD) {
            hasUseAction = packetByteBuf.readBoolean();
            hasReturnStack = packetByteBuf.readBoolean();
            hasSoundEvent = packetByteBuf.readBoolean();
            foodComponent = BackportedDataTypes.FOOD_COMPONENT.receive(packetByteBuf);
            if (hasUseAction) {
                useAction = UseAction.values()[packetByteBuf.readByte()];
            }
            if (hasReturnStack) {
                returnStack = packetByteBuf.readItemStack();
            }
            if (hasSoundEvent) {
                soundEventId = packetByteBuf.readIdentifier();
            }
        }
        FoodComponent finalFoodComponent = foodComponent;
        UseAction finalUseAction = useAction;
        ItemStack finalReturnStack = returnStack;
        Identifier finalSoundEventId = soundEventId;

        boolean usesEquipmentSlot = packetByteBuf.readBoolean();
        String equipmentSlotId = "";
        if (usesEquipmentSlot) {
            equipmentSlotId = packetByteBuf.readString(32767);
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
            } else {
                if (usesEquipmentSlot) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.byName(finalEquipmentSlotId);
                    ItemStack stack = ((PlayerEntity)entity).getEquippedStack(equipmentSlot);
                    switch (foodComponentAction) {
                        case ADD:
                            ItemStackFoodComponentAPI.setStackFood(stack, finalFoodComponent, finalUseAction, finalReturnStack, Registry.SOUND_EVENT.get(finalSoundEventId));
                            break;
                        case REMOVE:
                            ItemStackFoodComponentAPI.removeStackFood(stack);
                            break;
                    }
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
                    switch (foodComponentAction) {
                        case ADD:
                            ItemStackFoodComponentAPI.setStackFood(inventory.get(finalInventoryIndex), finalFoodComponent, finalUseAction, finalReturnStack, Registry.SOUND_EVENT.get(finalSoundEventId));
                            break;
                        case REMOVE:
                            ItemStackFoodComponentAPI.removeStackFood(inventory.get(finalInventoryIndex));
                            break;

                    }
                }
            }
        });
    }
}
