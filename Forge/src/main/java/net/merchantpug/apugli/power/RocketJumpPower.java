package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import com.mojang.serialization.Codec;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.damage.JumpExplosionPlayerDamageSource;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.networking.s2c.SyncRocketJumpExplosionPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.configuration.FabricActiveCooldownConfiguration;
import net.merchantpug.apugli.power.factory.RocketJumpPowerFactory;
import net.merchantpug.apugli.registry.ApugliTags;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.*;

import java.util.ArrayList;
import java.util.List;

@AutoService(RocketJumpPowerFactory.class)
public class RocketJumpPower extends AbstractActiveCooldownPower implements RocketJumpPowerFactory<ConfiguredPower<FabricActiveCooldownConfiguration, ?>> {

    protected RocketJumpPower(Codec<FabricActiveCooldownConfiguration> codec) {
        super(RocketJumpPowerFactory.getSerializableData().xmap(
                FabricActiveCooldownConfiguration::new,
                FabricActiveCooldownConfiguration::data
        ).codec());
    }

    @Override
    public void activate(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity player) {
        if (this.canUse(power, player)) {
            this.execute(power, player);
        }
    }

    @Override
    public void execute(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
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

            boolean isCharged = living.getActiveEffects().stream().anyMatch(effect -> Registry.MOB_EFFECT.getResourceKey(effect.getEffect()).isPresent() &&
                    Registry.MOB_EFFECT.getHolder(Registry.MOB_EFFECT.getResourceKey(effect.getEffect()).get()).isPresent() &&
                    Registry.MOB_EFFECT.getHolder(Registry.MOB_EFFECT.getResourceKey(effect.getEffect()).get()).get().is(ApugliTags.CHARGED_EFFECTS));

            if (blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return;

            if (entityHitResultType == HitResult.Type.ENTITY) {
                this.handleRocketJump(power, entity, entityHitResult, isCharged);
                return;
            }
            if (blockHitResultType == HitResult.Type.BLOCK) {
                this.handleRocketJump(power, entity, blockHitResult, isCharged);
            }
        }
    }

    private void handleRocketJump(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity, HitResult hitResult, boolean isCharged) {
        SerializableData.Instance data = getDataFromPower(power);
        boolean useCharged = data.getBoolean("use_charged");
        double horizontalVelocity = isCharged && useCharged && !chargedModifiers(power, entity).isEmpty() ? Services.PLATFORM.applyModifiers(entity, chargedModifiers(power, entity), data.getDouble("horizontal_velocity")) : data.getDouble("horizontal_velocity");
        double verticalVelocity = isCharged && useCharged && !chargedModifiers(power, entity).isEmpty() ? Services.PLATFORM.applyModifiers(entity, chargedModifiers(power, entity), data.getDouble("vertical_velocity")) : data.getDouble("vertical_velocity");
        float e = isCharged && useCharged ? 2.0F : 1.5F;
        if(data.isPresent("source") && data.getFloat("amount") != 0.0F) entity.hurt(data.get("source"), data.getFloat("amount"));
        float f = Mth.sin(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);
        float g = Mth.sin(entity.getXRot() * 0.017453292F);
        float h = -Mth.cos(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);

        Explosion explosion = new Explosion(entity.level, entity, new JumpExplosionPlayerDamageSource((LivingEntity) entity), null, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), e, false, Explosion.BlockInteraction.NONE);
        ((ExplosionAccess)explosion).setRocketJump(true);
        ((ExplosionAccess)explosion).setExplosionDamageModifiers(damageModifiers(power, entity));
        ((ExplosionAccess)explosion).setBiEntityPredicate(Services.CONDITION.biEntityPredicate(data, "damage_bientity_condition"));
        explosion.explode();
        explosion.finalizeExplosion(false);
        explosion.explode();
        explosion.finalizeExplosion(true);

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

    public void sendExplosionToClient(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity, HitResult hitResult, float radius) {
        SyncRocketJumpExplosionPacket packet = new SyncRocketJumpExplosionPacket(entity.getId(), hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), radius, power.getPowerType().getIdentifier());
        if (entity instanceof ServerPlayer serverPlayer)
            ApugliPacketHandler.sendS2CTrackingAndSelf(packet, serverPlayer);
    }

    @Override
    public SerializableData.Instance getDataFromPower(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power) {
        return power.getConfiguration().data();
    }

    @Override
    public List<ConfiguredModifier<?>> chargedModifiers(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        SerializableData.Instance data = getDataFromPower(power);
        List<ConfiguredModifier<?>> modifiers = new ArrayList<>();
        data.<List<ConfiguredModifier<?>>>ifPresent("charged_modifiers", modifiers::addAll);
        data.<ConfiguredModifier<?>>ifPresent("charged_modifier", modifiers::add);
        return modifiers;
    }

    @Override
    public List<?> waterModifiers(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        SerializableData.Instance data = getDataFromPower(power);
        List<ConfiguredModifier<?>> modifiers = new ArrayList<>();
        data.<List<ConfiguredModifier<?>>>ifPresent("water_modifiers", modifiers::addAll);
        data.<ConfiguredModifier<?>>ifPresent("water_modifier", modifiers::add);
        return modifiers;
    }

    @Override
    public List<?> damageModifiers(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        SerializableData.Instance data = getDataFromPower(power);
        List<ConfiguredModifier<?>> modifiers = new ArrayList<>();
        data.<List<ConfiguredModifier<?>>>ifPresent("damage_modifiers", modifiers::addAll);
        data.<ConfiguredModifier<?>>ifPresent("damage_modifier", modifiers::add);
        return modifiers;
    }
}
