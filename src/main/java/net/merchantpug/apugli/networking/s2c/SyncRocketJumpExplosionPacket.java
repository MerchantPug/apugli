package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.power.RocketJumpPower;
import net.merchantpug.apugli.registry.ApugliDamageSources;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.explosion.Explosion;

public record SyncRocketJumpExplosionPacket(int userId,
                                           double x,
                                           double y,
                                           double z,
                                           float radius,
                                           Identifier powerId) implements ApugliPacketS2C {
    public static final Identifier ID = Apugli.identifier("sync_rocket_jump_explosion");

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeInt(userId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(radius);
        buf.writeIdentifier(powerId);
    }

    public static SyncRocketJumpExplosionPacket decode(PacketByteBuf buf) {
        int userId = buf.readInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float radius = buf.readFloat();
        Identifier powerId = buf.readIdentifier();

        return new SyncRocketJumpExplosionPacket(userId, x, y, z, radius, powerId);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void handle(MinecraftClient client) {
        client.execute(() -> {
            Entity user = client.world.getEntityById(userId);
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
}
