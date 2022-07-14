/*
MIT License

Copyright (c) 2020 apace100

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


package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.util.BackportedDataTypes;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class SpawnParticlesAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        if(entity.world.isClient) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) entity.world;
        int count = data.get("count");
        if(count <= 0)
            return;
        float speed = data.get("speed");
        Vec3d spread = data.get("spread");
        float deltaX = (float) (entity.getWidth() * spread.x);
        float deltaY = (float) (entity.getHeight() * spread.y);
        float deltaZ = (float) (entity.getWidth() * spread.z);
        float offsetY = entity.getHeight() * data.getFloat("offset_y");
        serverWorld.spawnParticles(data.get("particle"), entity.getX(), entity.getY() + offsetY, entity.getZ(), count, deltaX, deltaY, deltaZ, speed);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("spawn_particles"),
            new SerializableData()
                .add("particle", BackportedDataTypes.PARTICLE_EFFECT_OR_TYPE)
                .add("count", SerializableDataType.INT)
                .add("speed", SerializableDataType.FLOAT, 0.0F)
                .add("force", SerializableDataType.BOOLEAN, false)
                .add("spread", BackportedDataTypes.VECTOR, new Vec3d(0.5, 0.25, 0.5))
                .add("offset_y", SerializableDataType.FLOAT, 0.5F),
            SpawnParticlesAction::action
        );
    }
}