package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.damage.JumpExplosionPlayerDamageSource;
import net.merchantpug.apugli.networking.ApugliPackets;
import net.merchantpug.apugli.networking.s2c.SyncRocketJumpExplosionPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.RocketJumpPowerFactory;
import net.merchantpug.apugli.registry.ApugliTags;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.*;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Deprecated
@AutoService(RocketJumpPowerFactory.class)
public class RocketJumpPower extends AbstractActiveCooldownPower<RocketJumpPower.Instance> implements RocketJumpPowerFactory<RocketJumpPower.Instance> {

    public RocketJumpPower(String id, SerializableData data, Function<SerializableData.Instance, BiFunction<PowerType<RocketJumpPower.Instance>, LivingEntity, RocketJumpPower.Instance>> factoryConstructor) {
        super(id, data, factoryConstructor);
    }

    @Override
    public SerializableData.Instance getDataFromPower(RocketJumpPower.Instance power) {
        return power.data;
    }

    @Override
    public Class<Instance> getPowerClass() {
        return Instance.class;
    }

    @Override
    public void sync(LivingEntity entity, RocketJumpPower.Instance power) {
        PowerHolderComponent.syncPower(entity, power.getType());
    }

    @Override
    public List<?> chargedModifiers(RocketJumpPower.Instance power, Entity entity) {
        return power.getChargedModifiers();
    }

    @Override
    public List<?> waterModifiers(RocketJumpPower.Instance power, Entity entity) {
        return power.getWaterModifiers();
    }

    @Override
    public List<?> damageModifiers(RocketJumpPower.Instance power, Entity entity) {
        return power.getDamageModifiers();
    }

    @Override
    public void execute(RocketJumpPower.Instance power, Entity entity) {
        power.onUse();
    }

    public static class Instance extends AbstractActiveCooldownPower.Instance {
        private final List<Modifier> chargedModifiers = new LinkedList<>();
        private final List<Modifier> waterModifiers = new LinkedList<>();
        private final List<Modifier> damageModifiers = new LinkedList<>();

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            data.ifPresent("charged_modifier", this::addChargedJumpModifier);
            data.<List<Modifier>>ifPresent("charged_modifiers", modifiers -> modifiers.forEach(this::addChargedJumpModifier));
            data.ifPresent("water_modifier", this::addWaterJumpModifier);
            data.<List<Modifier>>ifPresent("water_modifiers", modifiers -> modifiers.forEach(this::addWaterJumpModifier));
            data.ifPresent("damage_modifier", this::addDamageModifier);
            data.<List<Modifier>>ifPresent("damage_modifiers", modifiers -> modifiers.forEach(this::addDamageModifier));
        }

        @Override
        public void onUse() {
            if (canUse() && !entity.level.isClientSide()) {
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
                EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity, eyePosition, entityTraceEnd, entityBox, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.canBeCollidedWith() && (Services.CONDITION.biEntityPredicate(data, "targetable_bientity_condition") == null || Services.CONDITION.biEntityPredicate(data, "targetable_bientity_condition").test(new Tuple<>(entity, traceEntity))), entityReach);

                HitResult.Type blockHitResultType = blockHitResult.getType();
                HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;

                boolean isCharged = entity.getActiveEffects().stream().anyMatch(effect -> Registry.MOB_EFFECT.getResourceKey(effect.getEffect()).isPresent() &&
                        Registry.MOB_EFFECT.getHolder(Registry.MOB_EFFECT.getResourceKey(effect.getEffect()).get()).isPresent() &&
                        Registry.MOB_EFFECT.getHolder(Registry.MOB_EFFECT.getResourceKey(effect.getEffect()).get()).get().is(ApugliTags.CHARGED_EFFECTS));

                if (blockHitResultType == HitResult.Type.MISS && entityHitResultType == HitResult.Type.MISS) return;

                if (entityHitResultType == HitResult.Type.ENTITY) {
                    this.handleRocketJump(entityHitResult, isCharged);
                    return;
                }
                if (blockHitResultType == HitResult.Type.BLOCK) {
                    this.handleRocketJump(blockHitResult, isCharged);
                }
            }
        }

        private void handleRocketJump(HitResult hitResult, boolean isCharged) {
            boolean useCharged = data.getBoolean("use_charged");
            double horizontalVelocity = isCharged && useCharged && !this.getChargedModifiers().isEmpty() ? ModifierUtil.applyModifiers(entity, chargedModifiers, data.getDouble("horizontal_velocity")) : data.getDouble("horizontal_velocity");
            double verticalVelocity = isCharged && useCharged && !this.getChargedModifiers().isEmpty() ? ModifierUtil.applyModifiers(entity, chargedModifiers, data.getDouble("vertical_velocity")) : data.getDouble("vertical_velocity");
            float e = isCharged && useCharged ? 2.0F : 1.5F;
            if(data.get("source") != null && data.getInt("amount") != 0.0F) entity.hurt(data.get("source"), data.getInt("amount"));
            float f = Mth.sin(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);
            float g = Mth.sin(entity.getXRot() * 0.017453292F);
            float h = -Mth.cos(entity.getYRot() * 0.017453292F) * Mth.cos(entity.getXRot() * 0.017453292F);

            Explosion explosion = new Explosion(entity.level, entity, new JumpExplosionPlayerDamageSource(entity), null, hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), e, false, Explosion.BlockInteraction.NONE);
            ((ExplosionAccess)explosion).setRocketJump(true);
            ((ExplosionAccess)explosion).setExplosionDamageModifiers(this.getDamageModifiers());
            ((ExplosionAccess)explosion).setBiEntityPredicate(data.get("damage_bientity_condition"));
            explosion.explode();
            explosion.finalizeExplosion(false);
            explosion.explode();
            explosion.finalizeExplosion(true);

            sendExplosionToClient(hitResult, e);

            if(entity.isInWater()) {
                horizontalVelocity = !this.waterModifiers.isEmpty() ? ModifierUtil.applyModifiers(entity, waterModifiers, horizontalVelocity) : horizontalVelocity;
                verticalVelocity = !this.waterModifiers.isEmpty() ? ModifierUtil.applyModifiers(entity, waterModifiers, verticalVelocity) : verticalVelocity;
            }

            double velocityClampMultiplier = data.getDouble("velocity_clamp_multiplier");
            Vec3 vec = entity.getDeltaMovement().add(f * horizontalVelocity, g * verticalVelocity, h * horizontalVelocity);
            double horizontalClamp = isCharged ? ModifierUtil.applyModifiers(entity, getChargedModifiers(), horizontalVelocity * velocityClampMultiplier) : horizontalVelocity * velocityClampMultiplier;
            double verticalClamp = isCharged ? ModifierUtil.applyModifiers(entity, getChargedModifiers(), verticalVelocity * velocityClampMultiplier) : verticalVelocity * velocityClampMultiplier;
            entity.setDeltaMovement(Mth.clamp(vec.x, -horizontalClamp, horizontalClamp), Mth.clamp(vec.y, -verticalClamp, verticalClamp), Mth.clamp(vec.z, -horizontalClamp, horizontalClamp));
            entity.hurtMarked = true;
            entity.fallDistance = 0;
            this.use();
        }

        public void sendExplosionToClient(HitResult hitResult, float radius) {
            SyncRocketJumpExplosionPacket<?> packet = new SyncRocketJumpExplosionPacket<>(entity.getId(), hitResult.getLocation().x(), hitResult.getLocation().y(), hitResult.getLocation().z(), radius, data.get("damage_modifiers"), data.get("damage_bientity_condition"));

            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                ApugliPackets.sendS2C(packet, player);
            }
            if (!(entity instanceof ServerPlayer serverHolder)) return;
            ApugliPackets.sendS2C(packet, serverHolder);
        }

        public void addChargedJumpModifier(Modifier modifier) {
            this.chargedModifiers.add(modifier);
        }

        public List<Modifier> getChargedModifiers() {
            return chargedModifiers;
        }

        public void addWaterJumpModifier(Modifier modifier) {
            this.waterModifiers.add(modifier);
        }

        public List<Modifier> getWaterModifiers() {
            return waterModifiers;
        }

        public void addDamageModifier(Modifier modifier) {
            this.damageModifiers.add(modifier);
        }

        public List<Modifier> getDamageModifiers() {
            return damageModifiers;
        }

    }

}