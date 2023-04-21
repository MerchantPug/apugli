package net.merchantpug.apugli.networking.s2c;

import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.damage.JumpExplosionPlayerDamageSource;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;

import java.util.List;

public record SyncRocketJumpExplosionPacket<C>(int userId,
                                            double x,
                                            double y,
                                            double z,
                                            float radius,
                                            List<?> damageModifiers,
                                            C damageBiEntityCondition) implements ApugliPacketS2C {
    public static final ResourceLocation ID = Apugli.asResource("sync_rocket_jump_explosion");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(userId);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(radius);
        Services.PLATFORM.getModifiersDataType().send(buf, damageModifiers);
        Services.CONDITION.biEntityDataType().send(buf, damageBiEntityCondition);
    }

    public static <C> SyncRocketJumpExplosionPacket decode(FriendlyByteBuf buf) {
        int userId = buf.readInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        float radius = buf.readFloat();
        List<?> damageModifiers = Services.PLATFORM.getModifiersDataType().receive(buf);
        C damageBiEntityCondition = (C) Services.CONDITION.biEntityDataType().receive(buf);

        return new SyncRocketJumpExplosionPacket<>(userId, x, y, z, radius, damageModifiers, damageBiEntityCondition);
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
                Apugli.LOG.warn("Received unknown rocket jumping entity.");
            } else {
                Explosion explosion = new Explosion(user.level, user, new JumpExplosionPlayerDamageSource((LivingEntity) user), null, x, y, z, radius, false, Explosion.BlockInteraction.NONE);
                ((ExplosionAccess) explosion).setRocketJump(true);
                ((ExplosionAccess) explosion).setExplosionDamageModifiers(damageModifiers);
                ((ExplosionAccess) explosion).setBiEntityPredicate(damageBiEntityCondition);
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        });
    }
}
