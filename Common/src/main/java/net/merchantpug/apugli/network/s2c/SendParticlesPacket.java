package net.merchantpug.apugli.network.s2c;

import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.network.ApugliPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record SendParticlesPacket(ParticleOptions effect,
                                  boolean force,
                                  double x,
                                  double y,
                                  double z,
                                  float offsetX,
                                  float offsetY,
                                  float offsetZ,
                                  float speed,
                                  Optional<Vec3> velocity,
                                  int count) implements ApugliPacket {
    public static final ResourceLocation ID = Apugli.asResource("send_particles");

    @Override
    public void encode(FriendlyByteBuf buf) {
        SerializableDataTypes.PARTICLE_EFFECT.send(buf, effect);
        buf.writeBoolean(force);

        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);

        buf.writeFloat(offsetX);
        buf.writeFloat(offsetY);
        buf.writeFloat(offsetZ);

        buf.writeBoolean(velocity.isPresent());
        velocity.ifPresentOrElse((Vec3 vec3d) -> {
            buf.writeDouble(vec3d.x);
            buf.writeDouble(vec3d.y);
            buf.writeDouble(vec3d.z);
        }, () -> buf.writeFloat(speed));

        buf.writeInt(count);
    }

    public static SendParticlesPacket decode(FriendlyByteBuf buf) {
        ParticleOptions effect = SerializableDataTypes.PARTICLE_EFFECT.receive(buf);
        boolean force = buf.readBoolean();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float offsetX = buf.readFloat();
        float offsetY = buf.readFloat();
        float offsetZ = buf.readFloat();
        boolean hasVelocity = buf.readBoolean();

        float speed;
        Vec3 velocity;

        if (hasVelocity) {
            speed = 0.0F;
            velocity = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        } else {
            speed = buf.readFloat();
            velocity = null;
        }
        int count = buf.readInt();
        return new SendParticlesPacket(effect, force, x, y, z, offsetX, offsetY, offsetZ, speed, Optional.ofNullable(velocity), count);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static class Handler {
        public static void handle(SendParticlesPacket packet) {
            Minecraft.getInstance().execute(() -> {
                Level world = Minecraft.getInstance().level;
                if (world == null) {
                    Apugli.LOG.info("Could not find world to send particles to.");
                    return;
                }
                if (packet.count == 0) {
                    try {
                        double d = packet.velocity.isPresent() ? packet.velocity.get().x : packet.speed * packet.offsetX;
                        double e = packet.velocity.isPresent() ? packet.velocity.get().y : packet.speed * packet.offsetY;
                        double f = packet.velocity.isPresent() ? packet.velocity.get().z : packet.speed * packet.offsetZ;
                        world.addParticle(packet.effect, packet.force, packet.x, packet.y, packet.z, d, e, f);
                    } catch (Throwable throwable) {
                        Apugli.LOG.warn("Could not spawn particle effect {}", packet.effect);
                    }
                } else {
                    for (int i = 0; i < packet.count; ++i) {
                        double g = world.random.nextGaussian() * packet.offsetX;
                        double h = world.random.nextGaussian() * packet.offsetY;
                        double j = world.random.nextGaussian() * packet.offsetZ;
                        double k = packet.velocity.map(v -> v.x).orElseGet(() -> world.random.nextGaussian() * packet.speed);
                        double l = packet.velocity.map(v -> v.y).orElseGet(() -> world.random.nextGaussian() * packet.speed);
                        double m = packet.velocity.map(v -> v.z).orElseGet(() -> world.random.nextGaussian() * packet.speed);
                        try {
                            world.addParticle(packet.effect, packet.force, packet.x + g, packet.y + h, packet.z + j, k, l, m);
                        } catch (Throwable throwable2) {
                            Apugli.LOG.warn("Could not spawn particle effect {}", packet.effect);
                            return;
                        }
                    }
                }
            });
        }
    }
}
