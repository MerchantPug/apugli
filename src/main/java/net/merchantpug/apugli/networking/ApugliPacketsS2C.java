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

package net.merchantpug.apugli.networking;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.ApugliClient;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.power.RocketJumpPower;
import net.merchantpug.apugli.registry.ApugliDamageSources;
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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ApugliPacketsS2C {
    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientLoginNetworking.registerGlobalReceiver(ApugliPackets.HANDSHAKE, ApugliPacketsS2C::handleHandshake);
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(ApugliPackets.SEND_PARTICLES, ApugliPacketsS2C::onSendParticles);
            ClientPlayNetworking.registerReceiver(ApugliPackets.SEND_KEY_TO_CHECK, ApugliPacketsS2C::onSendPlayerKeybinds);
            ClientPlayNetworking.registerReceiver(ApugliPackets.SYNC_ROCKET_JUMP_EXPLOSION, ApugliPacketsS2C::onRocketJumpExplosionSync);
        }));
    }

    private static void onSendParticles(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        ParticleEffect effect = SerializableDataTypes.PARTICLE_EFFECT.receive(buf);
        boolean force = buf.readBoolean();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float offsetX = buf.readFloat();
        float offsetY = buf.readFloat();
        float offsetZ = buf.readFloat();
        boolean hasVelocity = buf.readBoolean();

        float speed;
        Vec3d velocity;

        if (hasVelocity) {
            speed = 0.0F;
            velocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        } else {
            speed = buf.readFloat();
            velocity = null;
        }
        float count = buf.readInt();

        client.execute(() -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world == null) {
                Apugli.LOGGER.info("Could not find world to send particles to.");
                return;
            }
            if (count == 0) {
                try {
                    double d = velocity != null ? velocity.x : speed * offsetX;
                    double e = velocity != null ? velocity.y : speed * offsetY;
                    double f = velocity != null ? velocity.z : speed * offsetZ;
                    world.addParticle(effect, force, x, y, z, d, e, f);
                }
                catch (Throwable throwable) {
                    Apugli.LOGGER.warn("Could not spawn particle effect {}", effect);
                }
            } else {
                for (int i = 0; i < count; ++i) {
                    double g = world.random.nextGaussian() * offsetX;
                    double h = world.random.nextGaussian() * offsetY;
                    double j = world.random.nextGaussian() * offsetZ;
                    double k = velocity != null ? velocity.x : world.random.nextGaussian() * speed;
                    double l = velocity != null ? velocity.y : world.random.nextGaussian() * speed;
                    double m = velocity != null ? velocity.z : world.random.nextGaussian() * speed;
                    try {
                        world.addParticle(effect, force, x + g, y + h, z + j, k, l, m);
                    }
                    catch (Throwable throwable2) {
                        Apugli.LOGGER.warn("Could not spawn particle effect {}", effect);
                        return;
                    }
                }
            }
        });
    }

    private static void onSendPlayerKeybinds(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        Active.Key key = ApoliDataTypes.KEY.receive(buf);

        client.execute(() -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (MinecraftClient.getInstance().player == null) {
                Apugli.LOGGER.warn("Could not find client player.");
                return;
            }
            KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
            component.getKeysToCheck().add(key);
            component.changePreviousKeysToCheckToCurrent();
        });
    }

    private static void onRocketJumpExplosionSync(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        int userId = buf.readInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float radius = buf.readFloat();
        Identifier powerId = buf.readIdentifier();

        client.execute(() -> {
            Entity user = handler.getWorld().getEntityById(userId);
            if (!(user instanceof LivingEntity)) {
                Apugli.LOGGER.warn("Received unknown rocket jumping entity.");
            } else {
                Explosion explosion = new Explosion(user.world, user, ApugliDamageSources.jumpExplosion((LivingEntity) user), null, x, y, z, radius, false, Explosion.DestructionType.NONE);
                Power power = PowerTypeRegistry.get(powerId).get(user);
                if (power instanceof RocketJumpPower rocketJumpPower) {
                    ((ExplosionAccess) explosion).setRocketJump(true);
                    ((ExplosionAccess) explosion).setExplosionDamageModifiers(rocketJumpPower.getDamageModifiers());
                    ((ExplosionAccess) explosion).setBiEntityPredicate(rocketJumpPower.getDamageBiEntityCondition());
                    explosion.collectBlocksAndDamageEntities();
                    explosion.affectWorld(true);
                } else {
                    Apugli.LOGGER.warn("Tried syncing rocket jump explosion without a valid RocketJumpPower.");
                }
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
