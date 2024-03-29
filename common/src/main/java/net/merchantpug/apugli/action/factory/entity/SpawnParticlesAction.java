package net.merchantpug.apugli.action.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.network.s2c.SendParticlesPacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class SpawnParticlesAction implements IActionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
                .add("count", SerializableDataTypes.INT)
                .add("speed", SerializableDataTypes.FLOAT, 0.0F)
                .add("force", SerializableDataTypes.BOOLEAN, false)
                .add("velocity", SerializableDataTypes.VECTOR, null)
                .add("spread", SerializableDataTypes.VECTOR, new Vec3(0.5, 0.25, 0.5))
                .add("offset_y", SerializableDataTypes.FLOAT, 0.5F);
    }

    @Override
    public void execute(SerializableData.Instance data, Entity instance) {
        if(instance.level().isClientSide) {
            return;
        }
        ServerLevel serverWorld = (ServerLevel) instance.level();
        int count = data.get("count");
        if(count <= 0)
            return;
        boolean force = data.get("force");
        float speed = data.get("speed");
        Vec3 spread = data.get("spread");
        float deltaX = (float) (instance.getBbWidth() * spread.x);
        float deltaY = (float) (instance.getBbHeight() * spread.y);
        float deltaZ = (float) (instance.getBbWidth() * spread.z);
        float offsetY = instance.getBbHeight() * data.getFloat("offset_y");
        Vec3 velocity = data.get("velocity");

        sendParticlePacket(serverWorld, data.get("particle"), force, instance.getX(), instance.getY() + offsetY, instance.getZ(), deltaX, deltaY, deltaZ, Optional.ofNullable(velocity), speed, count);
    }

    private void sendParticlePacket(ServerLevel world, ParticleOptions effect, boolean force, double x, double y, double z, float offsetX, float offsetY, float offsetZ, Optional<Vec3> velocity, float speed, int count) {
        for (int j = 0; j < world.players().size(); ++j) {
            ServerPlayer player = world.players().get(j);

            if (player.level() != world) return;
            BlockPos blockPos = player.getOnPos();
            if (blockPos.closerToCenterThan(new Vec3(x, y, z), force ? 512.0 : 32.0)) {
                Services.PLATFORM.sendS2C(new SendParticlesPacket(effect, force, x, y, z, offsetX, offsetY, offsetZ, speed, velocity, count), player);
            }
        }
    }
}
