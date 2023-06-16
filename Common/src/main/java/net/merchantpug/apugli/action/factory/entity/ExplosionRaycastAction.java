package net.merchantpug.apugli.action.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Space;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.access.ExplosionAccess;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.network.s2c.SyncExplosionPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.util.RaycastUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExplosionRaycastAction implements IActionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("distance", SerializableDataTypes.DOUBLE, null)
                .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
                .add("spacing", SerializableDataTypes.DOUBLE, 0.5)
                .add("direction", SerializableDataTypes.VECTOR, null)
                .add("space", ApoliDataTypes.SPACE, Space.WORLD)
                .add("block_action", Services.ACTION.blockDataType(), null)
                .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("action_on_hit", Services.ACTION.entityDataType(), null)
                .add("power", SerializableDataTypes.FLOAT)
                .add("destruction_type", ApoliDataTypes.BACKWARDS_COMPATIBLE_DESTRUCTION_TYPE, Explosion.BlockInteraction.KEEP)
                .add("damage_self", SerializableDataTypes.BOOLEAN, false)
                .add("indestructible", Services.CONDITION.blockDataType(), null)
                .add("destructible", Services.CONDITION.blockDataType(), null)
                .add("create_fire", SerializableDataTypes.BOOLEAN, false)
                .add("damage_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("damage_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("knockback_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("knockback_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("volume_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("volume_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("pitch_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("pitch_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("targetable_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("explosion_damage_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("use_charged", SerializableDataTypes.BOOLEAN, false)
                .add("charged_condition", Services.CONDITION.entityDataType(), null)
                .add("charged_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("charged_modifiers", Services.PLATFORM.getModifiersDataType(), null);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        if (entity.level.isClientSide) return;
        //Block Hit
        double blockDistance = data.isPresent("distance") ?
            data.getDouble("distance") :
            Services.PLATFORM.getReachDistance(entity);
        BlockHitResult blockHitResult = RaycastUtil.raycastBlock(entity, blockDistance, data.get("direction"), data.get("space"));
        HitResult.Type blockHitResultType = blockHitResult.getType();
        //Entity Hit
        double entityDistance = data.isPresent("distance") ?
            data.getDouble("distance") :
            Services.PLATFORM.getAttackRange(entity);
        EntityHitResult entityHitResult = RaycastUtil.raycastEntity(blockHitResult, entity, entityDistance, data.get("direction"), data.get("space"), Services.CONDITION.biEntityPredicate(data, "targetable_bientity_condition"));
        HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : HitResult.Type.MISS;

        double squaredParticleDistance = entityHitResultType != HitResult.Type.MISS ? entityHitResult.getLocation().distanceToSqr(entity.getEyePosition()) : blockDistance * blockDistance;
        createParticlesAtHitPos(data, entity, Math.sqrt(squaredParticleDistance));
        //Execute Actions
        if (entityHitResultType == HitResult.Type.ENTITY) {
            onHitEntity(data, entity, entityHitResult);
        } else if (blockHitResultType == HitResult.Type.BLOCK) {
            onHitBlock(data, entity, blockHitResult);
        }
    }
    
    protected void createParticlesAtHitPos(SerializableData.Instance data, Entity entity, double entityReach) {
        if(!data.isPresent("particle") || entity.level.isClientSide()) return;
        ParticleOptions particleEffect = data.get("particle");
        
        for(double d = data.getDouble("spacing"); d < entityReach; d += data.getDouble("spacing")) {
            ((ServerLevel)entity.level).sendParticles(particleEffect, entity.getEyePosition().x() + d * entity.getViewVector(0).x(), entity.getEyePosition().y() + d * entity.getViewVector(0).y(), entity.getEyePosition().z() + d * entity.getViewVector(0).z(), 1, 0, 0, 0, 0);
        }
    }

    protected void onHitBlock(SerializableData.Instance data, Entity entity, BlockHitResult result) {
        if(!data.isPresent("block_action") && !data.isPresent("action_on_hit")) return;
        Services.ACTION.executeBlock(data,"block_action", entity.level, result.getBlockPos(), result.getDirection());
        summonExplosion(data, entity, result);
        executeSelfAction(data, entity);
    }
    
    protected void onHitEntity(SerializableData.Instance data, Entity actor, EntityHitResult result) {
        if (!data.isPresent("bientity_action") && !data.isPresent("action_on_hit")) return;
        Entity target = result.getEntity();
        Services.ACTION.executeBiEntity(data, "bientity_action", actor, target);
        summonExplosion(data, actor, result);
        executeSelfAction(data, actor);
    }

    protected void executeSelfAction(SerializableData.Instance data, Entity entity) {
        if(!data.isPresent("action_on_hit") || !entity.isAlive()) return;
        Services.ACTION.executeEntity(data,"action_on_hit", entity);
    }

    protected float applyChargedModifiers(SerializableData.Instance data, Entity entity, float power) {
        List<?> chargedModifiers = getModifiers(data, "charged_modifier", "charged_modifiers");
        if (!chargedModifiers.isEmpty() && Services.CONDITION.checkEntity(data, "charged_condition", entity)) {
            return (float) Services.PLATFORM.applyModifiers(entity, chargedModifiers, data.getFloat("power"));
        }
        return power;
    }

    protected void summonExplosion(SerializableData.Instance data, Entity entity, HitResult result) {
        float power = data.getFloat("power");
        if (data.getBoolean("use_charged")) {
            power = applyChargedModifiers(data, entity, power);
        }
        boolean damageSelf = data.getBoolean("damage_self");
        boolean createFire = data.getBoolean("create_fire");
        Explosion.BlockInteraction destructionType = data.get("destruction_type");
        ExplosionDamageCalculator calculator = null;
        boolean indestructible = false;
        String blockConditionFieldKey = null;
        if (data.isPresent("destructible")) {
            calculator = createBlockConditionedExplosionDamageCalculator(data, "indestructible", entity.level, false);
            blockConditionFieldKey = "destructible";
        } else if (data.isPresent("indestructible")) {
            calculator = createBlockConditionedExplosionDamageCalculator(data, "destructible", entity.level, true);
            indestructible = true;
            blockConditionFieldKey = "indestructible";
        }
        if(calculator != null) {
            Explosion explosion = new Explosion(entity.level, damageSelf ? null : entity,
                    null,
                    calculator, result.getLocation().x(), result.getLocation().y(), result.getLocation().z(), power, createFire, destructionType);
            ((ExplosionAccess)explosion).setExplosionDamageModifiers(getModifiers(data, "damage_modifier", "damage_modifiers"));
            ((ExplosionAccess)explosion).setExplosionKnockbackModifiers(getModifiers(data, "knockback_modifier", "knockback_modifiers"));
            ((ExplosionAccess)explosion).setExplosionVolumeModifiers(getModifiers(data, "volume_modifier", "volume_modifiers"));
            ((ExplosionAccess)explosion).setExplosionPitchModifiers(getModifiers(data, "pitch_modifier", "pitch_modifiers"));
            ((ExplosionAccess)explosion).setBiEntityPredicate(data.get("explosion_damage_bientity_condition"));
            explosion.explode();
            explosion.finalizeExplosion(false);

            Services.PLATFORM.sendS2CTrackingAndSelf(new SyncExplosionPacket<>(
                    entity.getId(),result.getLocation().x(), result.getLocation().y(), result.getLocation().z(),
                    getModifiers(data, "damage_modifier", "damage_modifiers"),
                    getModifiers(data, "knockback_modifier", "knockback_modifiers"),
                    getModifiers(data, "volume_modifier", "volume_modifiers"),
                    getModifiers(data, "pitch_modifier", "pitch_modifiers"),
                    data.get("explosion_damage_bientity_condition"),
                    true,
                    data.get(blockConditionFieldKey),
                    indestructible,
                    createFire,
                    power,
                    data.get("destruction_type")), entity);
        } else {
            Explosion explosion = new Explosion(entity.level, damageSelf ? null : entity,
                    null, null,
                    result.getLocation().x(), result.getLocation().y(), result.getLocation().z(), power, createFire, destructionType);
            ((ExplosionAccess)explosion).setExplosionDamageModifiers(getModifiers(data, "damage_modifier", "damage_modifiers"));
            ((ExplosionAccess)explosion).setExplosionKnockbackModifiers(getModifiers(data, "knockback_modifier", "knockback_modifiers"));
            ((ExplosionAccess)explosion).setExplosionVolumeModifiers(getModifiers(data, "volume_modifier", "volume_modifiers"));
            ((ExplosionAccess)explosion).setExplosionPitchModifiers(getModifiers(data, "pitch_modifier", "pitch_modifiers"));
            ((ExplosionAccess)explosion).setBiEntityPredicate(data.get("explosion_damage_bientity_condition"));
            explosion.explode();
            explosion.finalizeExplosion(false);

            Services.PLATFORM.sendS2CTrackingAndSelf(new SyncExplosionPacket<>(
                    entity.getId(), result.getLocation().x(), result.getLocation().y(), result.getLocation().z(),
                    getModifiers(data, "damage_modifier", "damage_modifiers"),
                    getModifiers(data, "knockback_modifier", "knockback_modifiers"),
                    getModifiers(data, "volume_modifier", "volume_modifiers"),
                    getModifiers(data, "pitch_modifier", "pitch_modifiers"),
                    data.get("explosion_damage_bientity_condition"),
                    false,
                    null,
                    false,
                    createFire,
                    power,
                    data.get("destruction_type")), entity);
        }
    }

    private <M> List<M> getModifiers(SerializableData.Instance data, String modifierKey, String modifiersKey) {
        List<M> modifiers = new ArrayList<>();
        data.<List<M>>ifPresent(modifiersKey, modifiers::addAll);
        data.<M>ifPresent(modifierKey, modifiers::add);
        return modifiers;
    }

    private ExplosionDamageCalculator createBlockConditionedExplosionDamageCalculator(SerializableData.Instance data, String fieldName, Level levelIn, boolean indestructible) {
        return new ExplosionDamageCalculator() {
            @Override
            public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, FluidState fluidState) {
                Optional<Float> def = super.getBlockExplosionResistance(explosion, level, pos, blockState, fluidState);
                Optional<Float> ovr = Services.CONDITION.checkBlock(data, fieldName, levelIn, pos) == indestructible
                        ? Optional.of(Blocks.WATER.getExplosionResistance())
                        : Optional.empty();
                return ovr.isPresent() ? def.isPresent() ? def.get() > ovr.get() ? def : ovr : ovr : def;
            }
        };
    }

}
