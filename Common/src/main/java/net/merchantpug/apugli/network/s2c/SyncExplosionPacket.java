package net.merchantpug.apugli.network.s2c;

import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.network.ApugliPacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public record SyncExplosionPacket<BI, B>(int userId,
                                         double x,
                                         double y,
                                         double z,
                                         List<?> damageModifiers,
                                         List<?> knockbackModifiers,
                                         List<?> volumeModifiers,
                                         List<?> pitchModifiers,
                                         @Nullable BI biEntityConditions,
                                         boolean hasCalculator,
                                         @Nullable B blockConditions,
                                         boolean indestructible,
                                         boolean causesFire,
                                         float power,
                                         Explosion.BlockInteraction interaction) implements ApugliPacket {
    public static final ResourceLocation ID = Apugli.asResource("sync_explosion");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(userId());
        buf.writeDouble(x());
        buf.writeDouble(y());
        buf.writeDouble(z());
        Services.PLATFORM.getModifiersDataType().send(buf, damageModifiers());
        Services.PLATFORM.getModifiersDataType().send(buf, knockbackModifiers());
        Services.PLATFORM.getModifiersDataType().send(buf, volumeModifiers());
        Services.PLATFORM.getModifiersDataType().send(buf, pitchModifiers());

        buf.writeBoolean(biEntityConditions() != null);
        if (biEntityConditions() != null) {
            Services.CONDITION.biEntityDataType().send(buf, biEntityConditions());
        }

        buf.writeBoolean(hasCalculator());

        buf.writeBoolean(blockConditions() != null);
        if (blockConditions() != null) {
            Services.CONDITION.blockDataType().send(buf, blockConditions());
        }

        buf.writeBoolean(indestructible());
        buf.writeBoolean(causesFire());
        buf.writeFloat(power());
        SerializableDataTypes.DESTRUCTION_TYPE.send(buf, interaction());
    }

    public static <BI, B> SyncExplosionPacket<BI, B> decode(FriendlyByteBuf buf) {
        int userId = buf.readInt();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        List<?> damageModifiers = Services.PLATFORM.getModifiersDataType().receive(buf);
        List<?> knockbackModifiers = Services.PLATFORM.getModifiersDataType().receive(buf);
        List<?> volumeModifiers = Services.PLATFORM.getModifiersDataType().receive(buf);
        List<?> pitchModifiers = Services.PLATFORM.getModifiersDataType().receive(buf);
        boolean hasBiEntityCondition = buf.readBoolean();
        BI biEntityCondition = null;
        if (hasBiEntityCondition) {
            biEntityCondition = (BI) Services.CONDITION.biEntityDataType().receive(buf);
        }
        boolean hasCalculator = buf.readBoolean();

        boolean hasBlockDataCondition = buf.readBoolean();
        B blockDataCondition = null;
        if (hasBlockDataCondition) {
            blockDataCondition = (B) Services.CONDITION.blockDataType().receive(buf);
        }
        boolean indestructible = buf.readBoolean();
        boolean causesFire = buf.readBoolean();
        float radius = buf.readFloat();

        Explosion.BlockInteraction interaction = SerializableDataTypes.DESTRUCTION_TYPE.receive(buf);

        return new SyncExplosionPacket<>(userId, x, y, z, damageModifiers, knockbackModifiers, volumeModifiers, pitchModifiers, biEntityCondition, hasCalculator, blockDataCondition, indestructible, causesFire, radius, interaction);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static class Handler {
        public static void handle(SyncExplosionPacket<?, ?> packet) {
            Minecraft.getInstance().execute(() -> {
                Level level = Minecraft.getInstance().level;
                Entity entity = level.getEntity(packet.userId);
                Explosion explosion = new Explosion(level, entity,
                        entity instanceof LivingEntity living ?
                                DamageSource.explosion(living) :
                                DamageSource.explosion((LivingEntity) null), createBlockConditionedExplosionDamageCalculator(packet.blockConditions(), level, packet.indestructible), packet.x, packet.y, packet.z, packet.power, packet.causesFire, packet.interaction);
                ((ExplosionAccess) explosion).setExplosionDamageModifiers(packet.damageModifiers());
                ((ExplosionAccess) explosion).setExplosionKnockbackModifiers(packet.knockbackModifiers());
                ((ExplosionAccess) explosion).setExplosionVolumeModifiers(packet.volumeModifiers());
                ((ExplosionAccess) explosion).setExplosionPitchModifiers(packet.pitchModifiers());
                ((ExplosionAccess) explosion).setBiEntityPredicate(packet.biEntityConditions());
                explosion.explode();
                explosion.finalizeExplosion(true);
            });
        }

        private static <C> ExplosionDamageCalculator createBlockConditionedExplosionDamageCalculator(C blockCondition, Level levelIn, boolean indestructible) {
            return new ExplosionDamageCalculator() {
                @Override
                public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, FluidState fluidState) {
                    Optional<Float> def = super.getBlockExplosionResistance(explosion, level, pos, blockState, fluidState);
                    Optional<Float> ovr = Services.CONDITION.checkBlock(blockCondition, levelIn, pos) == indestructible
                            ? Optional.of(Blocks.WATER.getExplosionResistance())
                            : Optional.empty();
                    return ovr.isPresent() ? def.isPresent() ? def.get() > ovr.get() ? def : ovr : ovr : def;
                }
            };
        }
    }
}
