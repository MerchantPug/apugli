package io.github.merchantpug.apugli.networking;

import io.github.apace100.apoli.Apoli;
import io.github.merchantpug.apugli.util.ApugliSerializationHelper;
import io.github.merchantpug.apugli.util.StackFoodComponentUtil;
import io.github.merchantpug.nibbles.ItemStackFoodComponentAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(ApugliPackets.SYNC_STACK_FOOD_COMPONENT, ApugliPacketsS2C::onFoodComponentSync);
        }));
    }

    private static void onFoodComponentSync(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
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
            foodComponent = ApugliSerializationHelper.readFoodComponent(packetByteBuf);
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
            equipmentSlotId = packetByteBuf.readString(PacketByteBuf.DEFAULT_MAX_STRING_LENGTH);
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
        minecraftClient.execute(() -> {
            Entity entity = clientPlayNetworkHandler.getWorld().getEntityById(targetId);
            if (!(entity instanceof PlayerEntity)) {
                Apoli.LOGGER.warn("Received unknown target");
            } else {
                if (usesEquipmentSlot) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.byName(finalEquipmentSlotId);
                    ItemStack stack = ((PlayerEntity)entity).getEquippedStack(equipmentSlot);
                    switch (foodComponentAction) {
                        case ADD -> ItemStackFoodComponentAPI.setNibbles(stack, finalFoodComponent, finalUseAction, finalReturnStack, Registry.SOUND_EVENT.get(finalSoundEventId));
                        case REMOVE -> ItemStackFoodComponentAPI.removeNibbles(stack);
                    }
                }
                if (usesInventoryIndex) {
                    DefaultedList<ItemStack> inventory;
                    switch(finalInventoryLocation) {
                        case MAIN -> inventory = ((PlayerEntity) entity).getInventory().main;
                        case ARMOR -> inventory = ((PlayerEntity) entity).getInventory().armor;
                        case OFFHAND -> inventory = ((PlayerEntity) entity).getInventory().offHand;
                        default -> throw new IllegalStateException("Unexpected value: " + finalInventoryLocation);
                    }
                    switch (foodComponentAction) {
                        case ADD -> ItemStackFoodComponentAPI.setNibbles(inventory.get(finalInventoryIndex), finalFoodComponent, finalUseAction, finalReturnStack, Registry.SOUND_EVENT.get(finalSoundEventId));
                        case REMOVE -> ItemStackFoodComponentAPI.removeNibbles(inventory.get(finalInventoryIndex));
                    }
                }
            }
        });
    }
}
