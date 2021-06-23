package io.github.merchantpug.apugli.networking.packet;

import io.github.apace100.calio.mixin.DamageSourceAccessor;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.registry.ApugliDamageSources;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.explosion.Explosion;

public class RocketJumpPacket {
    public static final Identifier ID = Apugli.identifier("rocket_jump");

    public static void send(Vec3d pos, DamageSource damageSource, float damageAmount, boolean shouldUseCharged, double speed) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeDouble(pos.getX());
        buf.writeDouble(pos.getY());
        buf.writeDouble(pos.getZ());
        if (damageSource != null) {
            buf.writeBoolean(true);
            buf.writeString(damageSource.getName(), 325);
            buf.writeBoolean(damageSource.bypassesArmor());
            buf.writeBoolean(damageSource.isFire());
            buf.writeBoolean(damageSource.isUnblockable());
            buf.writeBoolean(damageSource.isMagic());
            buf.writeBoolean(damageSource.isOutOfWorld());
            buf.writeBoolean(damageSource.isExplosive());
            buf.writeBoolean(damageSource.isProjectile());
            buf.writeFloat(damageAmount);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeBoolean(shouldUseCharged);
        buf.writeDouble(speed);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        double crosshairX = buf.readDouble();
        double crosshairY = buf.readDouble();
        double crosshairZ = buf.readDouble();
        boolean doesDamage = buf.readBoolean();
        if (doesDamage) {
            DamageSource damageSource = DamageSourceAccessor.createDamageSource(buf.readString(32767));
            DamageSourceAccessor accessor = (DamageSourceAccessor)damageSource;
            if (buf.readBoolean()) {
                accessor.callSetBypassesArmor();
            }
            if (buf.readBoolean()) {
                accessor.callSetFire();
            }
            if (buf.readBoolean()) {
                accessor.callSetUnblockable();
            }
            if (buf.readBoolean()) {
                accessor.callSetUsesMagic();
            }
            if (buf.readBoolean()) {
                accessor.callSetOutOfWorld();
            }
            if (buf.readBoolean()) {
                accessor.callSetExplosive();
            }
            if (buf.readBoolean()) {
                accessor.callSetProjectile();
            }
            float damageAmount = buf.readFloat();

            if (damageSource != null && damageAmount != 0.0F) {
                player.damage(damageSource, damageAmount);
            }
        }
        boolean shouldUseCharged = buf.readBoolean();
        double speed = buf.readDouble();
        server.execute(() -> {
            boolean tmoCharged;
            boolean cursedCharged;
            if (FabricLoader.getInstance().isModLoaded("toomanyorigins")) {
                tmoCharged = player.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
            } else tmoCharged = false;
            if (FabricLoader.getInstance().isModLoaded("cursedorigins")) {
                cursedCharged = player.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
            } else cursedCharged = false;
            double d = (tmoCharged || cursedCharged) && shouldUseCharged ? 2.0D : 1.0D;
            float e = (tmoCharged || cursedCharged) && shouldUseCharged ? 2.0F : 1.5F;
            float f = MathHelper.sin(player.getYaw() * 0.017453292F) * MathHelper.cos(player.getPitch() * 0.017453292F);
            float g = MathHelper.sin(player.getPitch() * 0.017453292F);
            float h = -MathHelper.cos(player.getYaw() * 0.017453292F) * MathHelper.cos(player.getPitch() * 0.017453292F);

            player.world.createExplosion(player, ApugliDamageSources.jumpExplosion(player), null, crosshairX, crosshairY, crosshairZ, e, false, Explosion.DestructionType.NONE);
            player.addVelocity(f * speed * d, g * speed * d, h * speed * d);
            player.velocityModified = true;
            if (tmoCharged && shouldUseCharged) {
                player.removeStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
            }
            if (cursedCharged && shouldUseCharged) {
                player.removeStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
            }
        });
    }
}
