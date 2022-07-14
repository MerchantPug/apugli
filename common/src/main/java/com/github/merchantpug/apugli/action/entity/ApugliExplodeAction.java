package com.github.merchantpug.apugli.action.entity;

import com.github.merchantpug.apugli.Apugli;
import io.github.apace100.origins.power.factory.action.ActionFactory;
import io.github.apace100.origins.util.AttributeUtil;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ApugliExplodeAction {

    public static void action(SerializableData.Instance data, Entity entity) {
        if (entity.world.isClient) return;

        float power = data.getFloat("power");
        if (data.getBoolean("use_charged")) power = applyChargedModifiers(data, entity);

        summonExplosion(data, entity, power);
        spawnEffectCloud(data, entity);
    }

    private static float applyChargedModifiers(SerializableData.Instance data, Entity entity) {
        if (!data.getBoolean("use_charged")) return data.getFloat("power");
        boolean tmoCharged;
        boolean cursedCharged;
        List<EntityAttributeModifier> chargedModifiers = new ArrayList<>();
        if (data.isPresent("charged_modifier")) {
            chargedModifiers.add((EntityAttributeModifier)data.get("charged_modifier"));
        }
        if (data.isPresent("charged_modifiers")) {
            ((List<EntityAttributeModifier>)data.get("charged_modifiers")).forEach(modifier -> {
                chargedModifiers.add((EntityAttributeModifier)data.get("charged_modifier"));
            });
        }
        tmoCharged = FabricLoader.getInstance().isModLoaded("toomanyorigins") && ((LivingEntity) entity).hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
        cursedCharged = FabricLoader.getInstance().isModLoaded("cursedorigins") && ((LivingEntity) entity).hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));

        if (tmoCharged || cursedCharged) {
            ((LivingEntity)entity).removeStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
            ((LivingEntity)entity).removeStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
            return (float) AttributeUtil.applyModifiers(chargedModifiers, data.getFloat("power"));
        }
        return data.getFloat("power");
    }

    private static void summonExplosion(SerializableData.Instance data, Entity entity, float power) {
        if(data.isPresent("indestructible")) {
            Predicate<CachedBlockPosition> blockCondition = (Predicate<CachedBlockPosition>)data.get("indestructible");
            ExplosionBehavior eb = new ExplosionBehavior() {
                @Override
                public Optional<Float> getBlastResistance(Explosion explosion, BlockView blockView, BlockPos world, BlockState pos, FluidState blockState) {
                    Optional<Float> def = super.getBlastResistance(explosion, blockView, world, pos, blockState);
                    Optional<Float> ovr = blockCondition.test(
                            new CachedBlockPosition(entity.world, world, true)) ?
                            Optional.of(Blocks.WATER.getBlastResistance()) : Optional.empty();
                    return ovr.isPresent() ? def.isPresent() ? def.get() > ovr.get() ? def : ovr : ovr : def;
                }
            };
            entity.world.createExplosion(data.getBoolean("damage_self") ? null : entity,
                    entity instanceof LivingEntity ?
                            DamageSource.explosion((LivingEntity)entity) :
                            DamageSource.explosion((LivingEntity) null),
                    eb, entity.getX(), entity.getY(), entity.getZ(),
                    power, data.getBoolean("create_fire"),
                    (Explosion.DestructionType) data.get("destruction_type"));
        } else {
            entity.world.createExplosion(data.getBoolean("damage_self") ? null : entity,
                    entity.getX(), entity.getY(), entity.getZ(),
                    power, data.getBoolean("create_fire"),
                    (Explosion.DestructionType) data.get("destruction_type"));
        }
    }

    private static void spawnEffectCloud(SerializableData.Instance data, Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        Collection<StatusEffectInstance> collection = ((LivingEntity)entity).getStatusEffects();
        if (!collection.isEmpty() && data.getBoolean("spawn_effect_cloud")) {
            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
            areaEffectCloudEntity.setRadius(2.5F);
            areaEffectCloudEntity.setRadiusOnUse(-0.5F);
            areaEffectCloudEntity.setWaitTime(10);
            areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
            areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

            for (StatusEffectInstance statusEffectInstance : collection) {
                areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
            }
            entity.world.spawnEntity(areaEffectCloudEntity);
        }
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("explode"), new SerializableData()
                .add("power", SerializableDataType.FLOAT)
                .add("destruction_type", SerializableDataType.enumValue(Explosion.DestructionType.class), Explosion.DestructionType.BREAK)
                .add("damage_self", SerializableDataType.BOOLEAN, true)
                .add("indestructible", SerializableDataType.BLOCK_CONDITION, null)
                .add("destructible", SerializableDataType.BLOCK_CONDITION, null)
                .add("create_fire", SerializableDataType.BOOLEAN, false)
                .add("use_charged", SerializableDataType.BOOLEAN, false)
                .add("charged_modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                .add("charged_modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                .add("spawn_effect_cloud", SerializableDataType.BOOLEAN, false),
                ApugliExplodeAction::action
        );
    }
}
