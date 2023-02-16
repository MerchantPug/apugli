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

package net.merchantpug.apugli.action.entity;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.s2c.SendParticlesPacket;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class SpawnParticlesAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if(entity.world.isClient) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) entity.world;
        int count = data.get("count");
        if(count <= 0)
            return;
        boolean force = data.get("force");
        float speed = data.get("speed");
        Vec3d spread = data.get("spread");
        float deltaX = (float) (entity.getWidth() * spread.x);
        float deltaY = (float) (entity.getHeight() * spread.y);
        float deltaZ = (float) (entity.getWidth() * spread.z);
        float offsetY = entity.getHeight() * data.getFloat("offset_y");
        Vec3d velocity = data.get("velocity");

        sendParticlePacket(serverWorld, data.get("particle"), force, entity.getX(), entity.getY() + offsetY, entity.getZ(), deltaX, deltaY, deltaZ, Optional.ofNullable(velocity), speed, count);
    }

    private static void sendParticlePacket(ServerWorld world, ParticleEffect effect, boolean force, double x, double y, double z, float offsetX, float offsetY, float offsetZ, Optional<Vec3d> velocity, float speed, int count) {
        for (int j = 0; j < world.getPlayers().size(); ++j) {
            ServerPlayerEntity player = world.getPlayers().get(j);

            if (player.getWorld() != world) return;
            BlockPos blockPos = player.getBlockPos();
            if (blockPos.isWithinDistance(new Vec3d(x, y, z), force ? 512.0 : 32.0)) {
                ApugliPackets.sendS2C(new SendParticlesPacket(effect, force, x, y, z, offsetX, offsetY, offsetZ, speed, velocity, count), player);
            }
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("spawn_particles"),
                new SerializableData()
                        .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
                        .add("count", SerializableDataTypes.INT)
                        .add("speed", SerializableDataTypes.FLOAT, 0.0F)
                        .add("force", SerializableDataTypes.BOOLEAN, false)
                        .add("velocity", SerializableDataTypes.VECTOR, null)
                        .add("spread", SerializableDataTypes.VECTOR, new Vec3d(0.5, 0.25, 0.5))
                        .add("offset_y", SerializableDataTypes.FLOAT, 0.5F),
                io.github.apace100.apoli.power.factory.action.entity.SpawnParticlesAction::action
        );
    }
}
