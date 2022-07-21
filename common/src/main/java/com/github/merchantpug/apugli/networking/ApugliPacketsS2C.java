package com.github.merchantpug.apugli.networking;

import com.github.merchantpug.apugli.access.ExplosionAccess;
import com.github.merchantpug.apugli.registry.ApugliDamageSources;
import io.github.apace100.origins.power.Active;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import com.github.merchantpug.apugli.util.StackFoodComponentUtil;
import me.shedaniel.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.explosion.Explosion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class ApugliPacketsS2C {
    @Environment(EnvType.CLIENT)
    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), ApugliPackets.REMOVE_STACK_FOOD_COMPONENT, ApugliPacketsS2C::onFoodComponentSync);
        NetworkManager.registerReceiver(NetworkManager.s2c(), ApugliPackets.SYNC_ACTIVE_KEYS_CLIENT, ApugliPacketsS2C::onSyncActiveKeys);
        NetworkManager.registerReceiver(NetworkManager.s2c(), ApugliPackets.SYNC_ROCKET_JUMP_EXPLOSION, ApugliPacketsS2C::onSyncRocketjumpExplosion);
    }

    private static void onSyncRocketjumpExplosion(PacketByteBuf packetByteBuf, NetworkManager.PacketContext packetContext) {
        int userId = packetByteBuf.readInt();
        double x = packetByteBuf.readDouble();
        double y = packetByteBuf.readDouble();
        double z = packetByteBuf.readDouble();
        float radius = packetByteBuf.readFloat();

        packetContext.queue(() -> {
            Entity user = packetContext.getPlayer().getEntityWorld().getEntityById(userId);
            if (!(user instanceof LivingEntity)) {
                Apugli.LOGGER.warn("Received unknown target");
            } else {
                Explosion explosion = new Explosion(user.world, user, ApugliDamageSources.jumpExplosion((LivingEntity) user), null, x, y, z, radius, false, Explosion.DestructionType.NONE);
                ((ExplosionAccess) explosion).setRocketJump(true);
                explosion.collectBlocksAndDamageEntities();
                explosion.affectWorld(true);
            }
        });
    }

    private static void onSyncActiveKeys(PacketByteBuf packetByteBuf, NetworkManager.PacketContext packetContext) {
        int count = packetByteBuf.readInt();
        UUID playerUuid = packetByteBuf.readUuid();
        Active.Key[] activeKeys = new Active.Key[count];
        for(int i = 0; i < count; i++) {
            activeKeys[i] = SerializableDataType.KEY.receive(packetByteBuf);
        }
        packetContext.queue(() -> {
            Entity entity = packetContext.getPlayer().getEntityWorld().getPlayerByUuid(playerUuid);
            if (entity == null) {
                Apugli.LOGGER.warn("Failed modifying PlayerEntity's keys pressed.");
                return;
            }

            if (activeKeys.length == 0) {
                Apugli.currentlyUsedKeys.remove(entity.getUuid());
            } else {
                Apugli.currentlyUsedKeys.put(entity.getUuid(), new HashSet<>(Arrays.asList(activeKeys)));
            }
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
