package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.merchantpug.apugli.mixin.ServerPlayerEntityAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.explosion.Explosion;

import java.util.Collection;

public class DetonatePower extends Power implements Active {
    private final float explosionRadius;
    private final boolean spawnsEffectCloud;
    private final DamageSource damageSource;
    private final DamageSource selfDamageSource;
    private Key key;

    public DetonatePower(PowerType<?> type, LivingEntity entity, float explosionRadius, boolean spawnsEffectCloud, DamageSource damageSource, DamageSource selfDamageSource) {
        super(type, entity);
        this.explosionRadius = explosionRadius;
        this.spawnsEffectCloud = spawnsEffectCloud;
        this.damageSource = damageSource;
        this.selfDamageSource = selfDamageSource;
    }

    @Override
    public void onUse() {
        if (!entity.world.isClient) {
            if (entity instanceof PlayerEntity) {
                if (!((PlayerEntity)entity).getAbilities().invulnerable && ((ServerPlayerEntityAccessor)entity).getJoinInvulnerabilityTicks() <= 0) {
                    boolean tmoCharged;
                    boolean cursedCharged;
                    if (FabricLoader.getInstance().isModLoaded("toomanyorigins")) {
                        tmoCharged = entity.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
                    } else tmoCharged = false;
                    if (FabricLoader.getInstance().isModLoaded("cursedorigins")) {
                        cursedCharged = entity.hasStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
                    } else cursedCharged = false;
                    float f = (tmoCharged || cursedCharged) ? 2.0F : 1.0F;
                    Explosion.DestructionType destructionType = entity.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? Explosion.DestructionType.DESTROY : Explosion.DestructionType.NONE;

                    entity.world.createExplosion(entity, damageSource, null, entity.getX(), entity.getY(), entity.getZ(), explosionRadius * f, false, destructionType);

                    if (tmoCharged) {
                        entity.removeStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("toomanyorigins", "charged")));
                    }
                    if (cursedCharged) {
                        entity.removeStatusEffect(Registry.STATUS_EFFECT.get(new Identifier("cursedorigins", "charged")));
                    }

                    Collection<StatusEffectInstance> collection = entity.getStatusEffects();
                    if (!collection.isEmpty() && this.spawnsEffectCloud) {
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
                    entity.damage(selfDamageSource, Float.MAX_VALUE);
                }
            }

        }
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }
}
