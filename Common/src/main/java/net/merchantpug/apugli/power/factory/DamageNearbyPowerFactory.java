package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface DamageNearbyPowerFactory<P> extends CooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
                .add("damage_condition", Services.CONDITION.damageDataType(), null)
                .add("damage_type", SerializableDataTypes.DAMAGE_TYPE, null)
                .add("source", Services.PLATFORM.damageSourceDescriptionDataType(), null)
                .add("modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("radius", SerializableDataTypes.FLOAT, 16.0F);
    }

    default void execute(P power, LivingEntity powerHolder, DamageSource damageSource, float damageAmount, @Nullable Entity attacker, LivingEntity target,
                         String attackerName, String targetName, boolean damagerIsAttacker) {
        SerializableData.Instance data = getDataFromPower(power);
        if (canUse(power, powerHolder) && (!data.isPresent("damage_condition") || Services.CONDITION.checkDamage(data, "damage_condition", damageSource, damageAmount)) && (attacker == null && !data.isPresent(attackerName + "_" + targetName + "_bientity_condition") || Services.CONDITION.checkBiEntity(data, attackerName + "_" + targetName + "_bientity_condition", attacker, target))) {
            float radius = data.getFloat("radius");
            List<?> modifiers = new ArrayList<>();
            if (data.isPresent("modifiers"))
                modifiers = data.get("modifiers");

            if (data.isPresent("modifier"))
                modifiers.add(data.get("modifier"));

            for (LivingEntity nearby : target.level().getEntitiesOfClass(LivingEntity.class, AABB.ofSize(target.getPosition(1F), radius, radius, radius))) {
                if (nearby != attacker && nearby != target && (attacker == null && !data.isPresent(attackerName + "_" + targetName + "_bientity_condition") || Services.CONDITION.checkBiEntity(data, attackerName + "_nearby_bientity_condition", attacker, nearby)) && Services.CONDITION.checkBiEntity(data, targetName + "_nearby_bientity_condition", target, nearby)) {
                    if (attacker != null) {
                        nearby.hurt(Services.PLATFORM.createDamageSource(target.damageSources(), data, damagerIsAttacker ? attacker : powerHolder, "damage_type", "source"), (float) Services.PLATFORM.applyModifiers(powerHolder, modifiers, damageAmount));
                        if (data.isPresent(attackerName + "_" + targetName + "_bientity_action")) {
                            Services.ACTION.executeBiEntity(data, attackerName + "_" + targetName + "_bientity_action", attacker, target);
                        }
                        if (data.isPresent(attackerName + "_nearby_bientity_action")) {
                            Services.ACTION.executeBiEntity(data, attackerName + "_nearby_bientity_action", target, nearby);
                        }
                    } else {
                        nearby.hurt(Services.PLATFORM.createDamageSource(target.damageSources(), data, "damage_type", "source"), (float) Services.PLATFORM.applyModifiers(powerHolder, modifiers, damageAmount));
                    }
                    if (data.isPresent(targetName + "_nearby_bientity_action")) {
                        Services.ACTION.executeBiEntity(data, targetName + "_nearby_bientity_action", target, nearby);
                    }
                }
            }
            this.use(power, powerHolder);
        }
    }
}

