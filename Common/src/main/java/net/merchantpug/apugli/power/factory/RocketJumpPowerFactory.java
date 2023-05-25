package net.merchantpug.apugli.power.factory;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.networking.s2c.SyncRocketJumpExplosionPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.ApugliTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.*;

import java.util.List;

@Deprecated
public interface RocketJumpPowerFactory<P> extends ActiveCooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ActiveCooldownPowerFactory.getSerializableData()
                .add("distance", SerializableDataTypes.DOUBLE, Double.NaN)
                .add("damage_type", SerializableDataTypes.DAMAGE_TYPE, null)
                .add("source", ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, null)
                .add("amount", SerializableDataTypes.FLOAT, 0.0F)
                .add("velocity", SerializableDataTypes.DOUBLE, 1.0D)
                .addFunctionedDefault("horizontal_velocity", SerializableDataTypes.DOUBLE, data -> data.getDouble("velocity"))
                .addFunctionedDefault("vertical_velocity", SerializableDataTypes.DOUBLE, data -> data.getDouble("velocity"))
                .add("velocity_clamp_multiplier", SerializableDataTypes.DOUBLE, 1.8D)
                .add("use_charged", SerializableDataTypes.BOOLEAN, false)
                .add("charged_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("charged_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("water_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("water_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("damage_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("damage_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("targetable_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("damage_bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }

    List<?> chargedModifiers(P power, Entity entity);

    List<?> waterModifiers(P power, Entity entity);

    List<?> damageModifiers(P power, Entity entity);

    default void executeJump(P power, Entity entity) {
        try {
            if (entity instanceof LivingEntity living && !entity.level.isClientSide()) {
                SerializableData.Instance data = getDataFromPower(power);
                double distance = !Double.isNaN(data.getDouble("distance")) ? data.getDouble("distance") : Services.PLATFORM.getReachDistance(entity);
                Vec3 eyePosition = entity.getEyePosition(0);
                Vec3 lookVector = entity.getViewVector(0).scale(distance);
                Vec3 traceEnd = eyePosition.add(lookVector);

                ClipContext context = new ClipContext(eyePosition, traceEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity);
                BlockHitResult blockHitResult = entity.level.clip(context);

                double entityDistance = !Double.isNaN(data.getDouble("distance")) ? data.getDouble("distance") : Services.PLATFORM.getAttackRange(entity);
                Vec3 entityLookVector = entity.getViewVector(0).scale(entityDistance);
                Vec3 entityTraceEnd = eyePosition.add(entityLookVector);
                AABB entityBox = entity.getBoundingBox().expandTowards(lookVector).inflate(1.0D);

                double blockHitResultSquaredDistance = blockHitResult.getBlockPos().distToCenterSqr(eyePosition.x, eyePosition.y, eyePosition.z);
                double entityReach = Math.min(blockHitResultSquaredDistance, entityDistance * entityDistance);
                EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity, eyePosition, entityTraceEnd, entityBox, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.canBeCollidedWith() && (Services.CONDITION.checkBiEntity(data, "targetable_bientity_condition", entity, traceEntity)), entityReach);

                HitResult.Type blockHitResultType = blockHitResult.getType();
                HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;

                boolean isCharged = living.getActiveEffects().stream().anyMatch(effect -> BuiltInRegistries.MOB_EFFECT.getResourceKey(effect.getEffect()).isPresent() &&
                        BuiltInRegistries.MOB_EFFECT.getHolder(BuiltInRegistries.MOB_EFFECT.getResourceKey(effect.getEffect()).get()).isPresent() &&
                        BuiltInRegistries.MOB_EFFECT.getHolder(BuiltInRegistries.MOB_EFFECT.getResourceKey(effect.getEffect()).get()).get().is(ApugliTags.CHARGED_EFFECTS));

                if (blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return;

                if (entityHitResultType == HitResult.Type.ENTITY) {
                    this.handleRocketJump(power, living, entityHitResult, isCharged);
                    return;
                }
                if (blockHitResultType == HitResult.Type.BLOCK) {
                    this.handleRocketJump(power, living, blockHitResult, isCharged);
                }
            }
        } catch (Exception e) {
            Apugli.LOG.info("Error {}", e.getMessage());
        }
    }

    default void handleRocketJump(P power, LivingEntity entity, HitResult hitResult, boolean isCharged) {
        SerializableData.Instance data = getDataFromPower(power);
        boolean useCharged = data.getBoolean("use_charged");
        double horizontalVelocity = isCharged && useCharged && !chargedModifiers(power, entity).isEmpty() ? Services.PLATFORM.applyModifiers(entity, chargedModifiers(power, entity), data.getDouble("horizontal_velocity")) : data.getDouble("horizontal_velocity");
        double verticalVelocity = isCharged && useCharged && !chargedModifiers(power, entity).isEmpty() ? Services.PLATFORM.applyModifiers(entity, chargedModifiers(power, entity), data.getDouble("vertical_velocity")) : data.getDouble("vertical_velocity");
        float e = isCharged && useCharged ? 2.0F : 1.5F;
        DamageSource source = MiscUtil.createDamageSource(entity.damageSources(), data.get("source"), data.get("damage_type"));
        if(source != null && data.getFloat("amount") != 0.0F) entity.hurt(source, data.getFloat("amount"));
        float f = Mth.sin(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);
        float g = Mth.sin(entity.getXRot() * 0.017453292F);
        float h = -Mth.cos(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);

        Explosion explosion = new Explosion(entity.level, entity, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), e, false, Explosion.BlockInteraction.KEEP);
        ((ExplosionAccess)explosion).setRocketJump(true);
        ((ExplosionAccess)explosion).setExplosionDamageModifiers(damageModifiers(power, entity));
        ((ExplosionAccess)explosion).setBiEntityPredicate(Services.CONDITION.biEntityPredicate(data, "damage_bientity_condition"));
        explosion.explode();
        explosion.finalizeExplosion(false);

        sendExplosionToClient(power, entity, hitResult, e);

        if(entity.isInWater()) {
            horizontalVelocity = !waterModifiers(power, entity).isEmpty() ? Services.PLATFORM.applyModifiers(entity, waterModifiers(power, entity), horizontalVelocity) : horizontalVelocity;
            verticalVelocity = !waterModifiers(power, entity).isEmpty() ? Services.PLATFORM.applyModifiers(entity, waterModifiers(power, entity), verticalVelocity) : verticalVelocity;
        }

        double velocityClampMultiplier = data.getDouble("velocity_clamp_multiplier");
        Vec3 vec = entity.getDeltaMovement().add(f * horizontalVelocity, g * verticalVelocity, h * horizontalVelocity);
        double horizontalClamp = isCharged ? Services.PLATFORM.applyModifiers(entity, chargedModifiers(power, entity), horizontalVelocity * velocityClampMultiplier) : horizontalVelocity * velocityClampMultiplier;
        double verticalClamp = isCharged ? Services.PLATFORM.applyModifiers(entity, chargedModifiers(power, entity), verticalVelocity * velocityClampMultiplier) : verticalVelocity * velocityClampMultiplier;
        entity.setDeltaMovement(Mth.clamp(vec.x, -horizontalClamp, horizontalClamp), Mth.clamp(vec.y, -verticalClamp, verticalClamp), Mth.clamp(vec.z, -horizontalClamp, horizontalClamp));
        entity.hurtMarked = true;
        entity.fallDistance = 0;
        this.use(power, entity);
    }

    default void sendExplosionToClient(P power, LivingEntity entity, HitResult hitResult, float radius) {
        SyncRocketJumpExplosionPacket packet = new SyncRocketJumpExplosionPacket(entity.getId(), hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), radius, Services.POWER.getIdFromPower(entity, power));
        if (entity instanceof ServerPlayer serverPlayer)
            Services.PLATFORM.sendS2CTrackingAndSelf(packet, serverPlayer);
    }

}
