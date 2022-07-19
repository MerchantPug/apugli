/*
MIT License

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

package com.github.merchantpug.apugli.networking;

import com.github.merchantpug.apugli.util.HitsOnTargetUtil;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.ApugliClient;
import com.github.merchantpug.apugli.access.ExplosionAccess;
import com.github.merchantpug.apugli.access.LivingEntityAccess;
import com.github.merchantpug.apugli.registry.ApugliDamageSources;
import com.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import com.github.merchantpug.apugli.util.StackFoodComponentUtil;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.explosion.Explosion;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ApugliPacketsS2C {
    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientLoginNetworking.registerGlobalReceiver(ApugliPackets.HANDSHAKE, ApugliPacketsS2C::handleHandshake);
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(ApugliPackets.REMOVE_STACK_FOOD_COMPONENT, ApugliPacketsS2C::onFoodComponentSync);
            ClientPlayNetworking.registerReceiver(ApugliPackets.SYNC_HITS_ON_TARGET, ApugliPacketsS2C::onHitsOnTargetSync);
            ClientPlayNetworking.registerReceiver(ApugliPackets.SYNC_ACTIVE_KEYS_CLIENT, ApugliPacketsS2C::onSyncActiveKeys);
            ClientPlayNetworking.registerReceiver(ApugliPackets.SYNC_ROCKET_JUMP_EXPLOSION, ApugliPacketsS2C::syncRocketJumpExplosion);
        }));
    }

    private static void onSyncActiveKeys(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int count = packetByteBuf.readInt();
        int playerId = packetByteBuf.readInt();
        Active.Key[] activeKeys = new Active.Key[count];
        for(int i = 0; i < count; i++) {
            activeKeys[i] = ApoliDataTypes.KEY.receive(packetByteBuf);
        }
        minecraftClient.execute(() -> {
            Entity entity = clientPlayNetworkHandler.getWorld().getEntityById(playerId);
            if (!(entity instanceof PlayerEntity playerEntity)) {
                Apugli.LOGGER.warn("Tried modifying non PlayerEntity's keys pressed.");
                return;
            }
            if (activeKeys.length == 0) {
                Apugli.currentlyUsedKeys.remove(playerEntity.getUuid());
            } else {
                Apugli.currentlyUsedKeys.put(playerEntity.getUuid(), new HashSet<>(List.of(activeKeys)));
            }
        });
    }

    private static void onHitsOnTargetSync(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        HitsOnTargetUtil.PacketType type = HitsOnTargetUtil.PacketType.values()[packetByteBuf.readByte()];
        int targetId = packetByteBuf.readInt();
        int attackerId = 0;
        boolean hasAttacker = packetByteBuf.readBoolean();
        if (hasAttacker) {
            attackerId = packetByteBuf.readInt();
        }
        int amount = 0;
        int timer = 0;
        if (type == HitsOnTargetUtil.PacketType.SET) {
            amount = packetByteBuf.readInt();
            timer = packetByteBuf.readInt();
        }
        int finalAttackerId = attackerId;
        int finalAmount = amount;
        int finalTimer = timer;

        minecraftClient.execute(() -> {
            Entity target = clientPlayNetworkHandler.getWorld().getEntityById(targetId);
            Entity attacker = null;
            if (hasAttacker) {
               attacker = clientPlayNetworkHandler.getWorld().getEntityById(finalAttackerId);
            }
            if (!(target instanceof LivingEntity)) {
                Apugli.LOGGER.warn("Received unknown target");
            } else if (hasAttacker && attacker == null) {
                Apugli.LOGGER.warn("Received unknown attacker");
            } else switch (type) {
                case SET -> ((LivingEntityAccess)target).getHits().put(attacker, new Pair<>(finalAmount, finalTimer));
                case REMOVE -> {
                    if (!((LivingEntityAccess)target).getHits().containsKey(attacker)) return;
                    ((LivingEntityAccess)target).getHits().remove(attacker);
                }
                case CLEAR -> ((LivingEntityAccess)target).getHits().clear();
            }
        });
    }

    private static void onFoodComponentSync(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int targetId = packetByteBuf.readInt();

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
                Apugli.LOGGER.warn("Received unknown target");
            } else {
                if (usesEquipmentSlot) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.byName(finalEquipmentSlotId);
                    ItemStack stack = ((PlayerEntity)entity).getEquippedStack(equipmentSlot);
                    ItemStackFoodComponentUtil.removeStackFood(stack);
                }
                if (usesInventoryIndex) {
                    DefaultedList<ItemStack> inventory;
                    switch(finalInventoryLocation) {
                        case MAIN -> inventory = ((PlayerEntity) entity).getInventory().main;
                        case ARMOR -> inventory = ((PlayerEntity) entity).getInventory().armor;
                        case OFFHAND -> inventory = ((PlayerEntity) entity).getInventory().offHand;
                        default -> throw new IllegalStateException("Unexpected value: " + finalInventoryLocation);
                    }
                    ItemStackFoodComponentUtil.removeStackFood(inventory.get(finalInventoryIndex));
                }
            }
        });
    }

    private static void syncRocketJumpExplosion(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        int userId = packetByteBuf.readInt();
        double x = packetByteBuf.readDouble();
        double y = packetByteBuf.readDouble();
        double z = packetByteBuf.readDouble();
        float radius = packetByteBuf.readFloat();
        List<Modifier> damageModifiers = Modifier.LIST_TYPE.receive(packetByteBuf);

        minecraftClient.execute(() -> {
            Entity user = clientPlayNetworkHandler.getWorld().getEntityById(userId);
            if (!(user instanceof LivingEntity)) {
                Apugli.LOGGER.warn("Received unknown target");
            } else {
                Explosion explosion = new Explosion(user.world, user, ApugliDamageSources.jumpExplosion((LivingEntity) user), null, x, y, z, radius, false, Explosion.DestructionType.NONE);
                ((ExplosionAccess) explosion).setRocketJump(true);
                ((ExplosionAccess) explosion).setExplosionDamageModifiers(damageModifiers);
                explosion.collectBlocksAndDamageEntities();
                explosion.affectWorld(true);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    private static CompletableFuture<PacketByteBuf> handleHandshake(MinecraftClient minecraftClient, ClientLoginNetworkHandler clientLoginNetworkHandler, PacketByteBuf packetByteBuf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Apugli.SEMVER.length);
        for(int i = 0; i < Apugli.SEMVER.length; i++) {
            buf.writeInt(Apugli.SEMVER[i]);
        }
        ApugliClient.isServerRunningApugli = true;
        return CompletableFuture.completedFuture(buf);
    }
}
