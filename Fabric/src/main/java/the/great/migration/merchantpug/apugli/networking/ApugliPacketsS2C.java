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

package the.great.migration.merchantpug.apugli.networking;

import com.github.merchantpug.apugli.access.ExplosionAccess;
import com.github.merchantpug.apugli.access.LivingEntityAccess;
import com.github.merchantpug.apugli.entity.damage.JumpExplosionDamageSource;
import com.github.merchantpug.apugli.entity.damage.JumpExplosionPlayerDamageSource;
import com.github.merchantpug.apugli.util.HitsOnTargetUtil;
import com.github.merchantpug.apugli.util.ItemStackFoodComponentUtil;
import com.github.merchantpug.apugli.util.StackFoodComponentUtil;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import the.great.migration.merchantpug.apugli.Apugli;
import the.great.migration.merchantpug.apugli.ApugliClient;

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

    private static void onSyncActiveKeys(Minecraft minecraftClient, ClientPacketListener clientPlayNetworkHandler, FriendlyByteBuf packetByteBuf, PacketSender packetSender) {
        int count = packetByteBuf.readInt();
        int playerId = packetByteBuf.readInt();
        Active.Key[] activeKeys = new Active.Key[count];
        for(int i = 0; i < count; i++) {
            activeKeys[i] = ApoliDataTypes.KEY.receive(packetByteBuf);
        }
        minecraftClient.execute(() -> {
            Entity entity = clientPlayNetworkHandler.getLevel().getEntity(playerId);
            if(!(entity instanceof Player playerEntity)) {
                Apugli.LOGGER.warn("Tried modifying non PlayerEntity's keys pressed.");
                return;
            }
            if(activeKeys.length == 0) {
                Apugli.currentlyUsedKeys.remove(playerEntity.getUUID());
            } else {
                Apugli.currentlyUsedKeys.put(playerEntity.getUUID(), new HashSet<>(List.of(activeKeys)));
            }
        });
    }

    private static void onHitsOnTargetSync(Minecraft minecraftClient, ClientPacketListener clientPlayNetworkHandler, FriendlyByteBuf packetByteBuf, PacketSender packetSender) {
        HitsOnTargetUtil.PacketType type = HitsOnTargetUtil.PacketType.values()[packetByteBuf.readByte()];
        int targetId = packetByteBuf.readInt();
        int attackerId = 0;
        boolean hasAttacker = packetByteBuf.readBoolean();
        if(hasAttacker) {
            attackerId = packetByteBuf.readInt();
        }
        int amount = 0;
        int timer = 0;
        if(type == HitsOnTargetUtil.PacketType.SET) {
            amount = packetByteBuf.readInt();
            timer = packetByteBuf.readInt();
        }
        int finalAttackerId = attackerId;
        int finalAmount = amount;
        int finalTimer = timer;

        minecraftClient.execute(() -> {
            Entity target = clientPlayNetworkHandler.getLevel().getEntity(targetId);
            Entity attacker = null;
            if(hasAttacker) {
               attacker = clientPlayNetworkHandler.getLevel().getEntity(finalAttackerId);
            }
            if(!(target instanceof LivingEntity)) {
                Apugli.LOGGER.warn("Received unknown target");
            } else if(hasAttacker && attacker == null) {
                Apugli.LOGGER.warn("Received unknown attacker");
            } else switch (type) {
                case SET -> ((LivingEntityAccess)target).getHits().put(attacker, new Tuple<>(finalAmount, finalTimer));
                case REMOVE -> {
                    if(!((LivingEntityAccess)target).getHits().containsKey(attacker)) return;
                    ((LivingEntityAccess)target).getHits().remove(attacker);
                }
                case CLEAR -> ((LivingEntityAccess)target).getHits().clear();
            }
        });
    }

    private static void onFoodComponentSync(Minecraft minecraftClient, ClientPacketListener clientPlayNetworkHandler, FriendlyByteBuf packetByteBuf, PacketSender packetSender) {
        int targetId = packetByteBuf.readInt();

        boolean usesEquipmentSlot = packetByteBuf.readBoolean();
        String equipmentSlotId = "";
        if(usesEquipmentSlot) {
            equipmentSlotId = packetByteBuf.readUtf(FriendlyByteBuf.MAX_STRING_LENGTH);
        }
        String finalEquipmentSlotId = equipmentSlotId;

        boolean usesInventoryIndex = packetByteBuf.readBoolean();
        StackFoodComponentUtil.InventoryLocation inventoryLocation = null;
        int inventoryIndex = 0;
        if(usesInventoryIndex) {
            inventoryLocation = StackFoodComponentUtil.InventoryLocation.values()[packetByteBuf.readByte()];
            inventoryIndex = packetByteBuf.readInt();
        }
        StackFoodComponentUtil.InventoryLocation finalInventoryLocation = inventoryLocation;
        int finalInventoryIndex = inventoryIndex;

        minecraftClient.execute(() -> {
            Entity entity = clientPlayNetworkHandler.getLevel().getEntity(targetId);
            if(!(entity instanceof Player)) {
                Apugli.LOGGER.warn("Received unknown target");
            } else {
                if(usesEquipmentSlot) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.byName(finalEquipmentSlotId);
                    ItemStack stack = ((Player)entity).getItemBySlot(equipmentSlot);
                    ItemStackFoodComponentUtil.removeStackFood(stack);
                }
                if(usesInventoryIndex) {
                    NonNullList<ItemStack> inventory;
                    switch(finalInventoryLocation) {
                        case MAIN -> inventory = ((Player) entity).getInventory().items;
                        case ARMOR -> inventory = ((Player) entity).getInventory().armor;
                        case OFFHAND -> inventory = ((Player) entity).getInventory().offhand;
                        default -> throw new IllegalStateException("Unexpected value: " + finalInventoryLocation);
                    }
                    ItemStackFoodComponentUtil.removeStackFood(inventory.get(finalInventoryIndex));
                }
            }
        });
    }

    private static void syncRocketJumpExplosion(Minecraft minecraftClient, ClientPacketListener clientPlayNetworkHandler, FriendlyByteBuf packetByteBuf, PacketSender packetSender) {
        int userId = packetByteBuf.readInt();
        double x = packetByteBuf.readDouble();
        double y = packetByteBuf.readDouble();
        double z = packetByteBuf.readDouble();
        float radius = packetByteBuf.readFloat();
        List<Modifier> damageModifiers = Modifier.LIST_TYPE.receive(packetByteBuf);

        minecraftClient.execute(() -> {
            Entity user = clientPlayNetworkHandler.getLevel().getEntity(userId);
            if(!(user instanceof LivingEntity)) {
                Apugli.LOGGER.warn("Received unknown target");
            } else {
                Explosion explosion = new Explosion(user.level, user,
                    user instanceof LivingEntity living
                        ? new JumpExplosionPlayerDamageSource(living)
                        : new JumpExplosionDamageSource(),
                    null, x, y, z, radius, false, Explosion.BlockInteraction.NONE);
                ((ExplosionAccess) explosion).setRocketJump(true);
                ((ExplosionAccess) explosion).setExplosionDamageModifiers(damageModifiers);
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        });
    }

    @Environment(EnvType.CLIENT)
    private static CompletableFuture<FriendlyByteBuf> handleHandshake(Minecraft minecraftClient, ClientHandshakePacketListenerImpl clientLoginNetworkHandler, FriendlyByteBuf packetByteBuf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Apugli.SEMVER.length);
        for(int i = 0; i < Apugli.SEMVER.length; i++) {
            buf.writeInt(Apugli.SEMVER[i]);
        }
        ApugliClient.isServerRunningApugli = true;
        return CompletableFuture.completedFuture(buf);
    }
}
