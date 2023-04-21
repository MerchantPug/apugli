package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;

public record SyncRocketJumpExplosionPacket(int userId,
                                            double x,
                                            double y,
                                            double z,
                                            float radius,
                                            ResourceLocation powerId) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("sync_rocket_jump_explosion");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(userId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(radius);
        buf.writeResourceLocation(powerId);
    }

    public static SyncRocketJumpExplosionPacket decode(FriendlyByteBuf buf) {
        int userId = buf.readInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float radius = buf.readFloat();
        ResourceLocation powerId = buf.readResourceLocation();

        return new SyncRocketJumpExplosionPacket(userId, x, y, z, radius, powerId);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity user = Minecraft.getInstance().level.getEntity(userId);
            if (!(user instanceof LivingEntity)) {
                Apugli.LOGGER.warn("Received unknown rocket jumping entity.");
            } else {
                Explosion explosion = new Explosion(user.level, user, ApugliDamageSources.jumpExplosion((LivingEntity) user), null, x, y, z, radius, false, Explosion.BlockInteraction.NONE);
                Power power = PowerTypeRegistry.get(powerId).get(user);
                if (power instanceof RocketJumpPower rocketJumpPower) {
                    ((ExplosionAccess) explosion).setRocketJump(true);
                    ((ExplosionAccess) explosion).setExplosionDamageModifiers(rocketJumpPower.getDamageModifiers());
                    ((ExplosionAccess) explosion).setBiEntityPredicate(rocketJumpPower.getDamageBiEntityCondition());
                    explosion.explode();
                    explosion.finalizeExplosion(true);
                } else {
                    Apugli.LOGGER.warn("Tried syncing rocket jump explosion without a valid RocketJumpPower.");
                }
            }
        });
    }
}
