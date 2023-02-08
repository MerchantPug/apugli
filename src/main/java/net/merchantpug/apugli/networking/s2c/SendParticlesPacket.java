package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public record SendParticlesPacket(ParticleEffect effect,
                                  boolean force,
                                  double x,
                                  double y,
                                  double z,
                                  float offsetX,
                                  float offsetY,
                                  float offsetZ,
                                  float speed,
                                  Optional<Vec3d> velocity,
                                  int count) implements ApugliPacketS2C {
    public static final Identifier ID = Apugli.identifier("send_particles");

    @Override
    public void encode(PacketByteBuf buf) {
        SerializableDataTypes.PARTICLE_EFFECT.send(buf, effect);
        buf.writeBoolean(force);

        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);

        buf.writeFloat(offsetX);
        buf.writeFloat(offsetY);
        buf.writeFloat(offsetZ);

        buf.writeBoolean(velocity.isPresent());
        velocity.ifPresentOrElse((Vec3d vec3d) -> {
            buf.writeDouble(vec3d.x);
            buf.writeDouble(vec3d.y);
            buf.writeDouble(vec3d.z);
        }, () -> buf.writeFloat(speed));

        buf.writeInt(count);
    }

    public static SendParticlesPacket decode(PacketByteBuf buf) {
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
        int count = buf.readInt();
        return new SendParticlesPacket(effect, force, x, y, z, offsetX, offsetY, offsetZ, speed, Optional.ofNullable(velocity), count);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void handle(MinecraftClient client) {
        client.execute(() -> {
            ClientWorld world = MinecraftClient.getInstance().world;
            if (world == null) {
                Apugli.LOGGER.info("Could not find world to send particles to.");
                return;
            }
            if (count == 0) {
                try {
                    double d = velocity.isPresent() ? velocity.get().x : speed * offsetX;
                    double e = velocity.isPresent() ? velocity.get().y : speed * offsetY;
                    double f = velocity.isPresent() ? velocity.get().z : speed * offsetZ;
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
                    double k = velocity.map(v -> v.x).orElseGet(() -> world.random.nextGaussian() * speed);
                    double l = velocity.map(v -> v.y).orElseGet(() -> world.random.nextGaussian() * speed);
                    double m = velocity.map(v -> v.z).orElseGet(() -> world.random.nextGaussian() * speed);
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
}
