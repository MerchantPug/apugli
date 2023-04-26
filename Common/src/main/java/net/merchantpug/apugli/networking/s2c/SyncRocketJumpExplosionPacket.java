package net.merchantpug.apugli.networking.s2c;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.damage.JumpExplosionPlayerDamageSource;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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
        ResourceLocation power = buf.readResourceLocation();

        return new SyncRocketJumpExplosionPacket(userId, x, y, z, radius, power);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void handle() {
        Minecraft.getInstance().execute(() -> {
            Entity user = Minecraft.getInstance().level.getEntity(userId);
            if (!(user instanceof LivingEntity living)) {
                Apugli.LOG.warn("Received unknown rocket jumping entity.");
            } else {
                PowerType<?> powerType = PowerTypeRegistry.get(powerId);
                Explosion explosion = new Explosion(user.level, user, new JumpExplosionPlayerDamageSource(living), null, x, y, z, radius, false, Explosion.BlockInteraction.NONE);
                ((ExplosionAccess) explosion).setRocketJump(true);
                ((ExplosionAccess) explosion).setExplosionDamageModifiers(ApugliPowers.ROCKET_JUMP.get().damageModifiers(Services.POWER.getPowerFromType(living, powerType), living));
                ((ExplosionAccess) explosion).setBiEntityPredicate(Services.CONDITION.biEntityPredicate(ApugliPowers.ROCKET_JUMP.get().getDataFromPower(Services.POWER.getPowerFromType(living, powerType)), "damage_bientity_condition"));
                explosion.explode();
                explosion.finalizeExplosion(true);
            }
        });
    }

}
