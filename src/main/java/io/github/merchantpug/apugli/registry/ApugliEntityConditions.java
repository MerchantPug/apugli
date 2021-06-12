package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;

public class ApugliEntityConditions {
    public static void register() {
        register(new ConditionFactory<>(Apugli.identifier("nearby_entities"), new SerializableData()
                .add("entity_type", SerializableDataTypes.ENTITY_TYPE)
                .add("player_box_multiplier", SerializableDataTypes.FLOAT)
                .add("comparison", ApoliDataTypes.COMPARISON)
                .add("compare_to", SerializableDataTypes.INT),
                (data, entity) -> {
                    EntityType<?> entityType = (EntityType<?>) data.get("entity_type");
                    Float playerBoxMultiplier = (Float) data.get("player_box_multiplier");
                    int amount = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(playerBoxMultiplier), typeOfEntity -> typeOfEntity.getType() == entityType).size();
                    Comparison comparison = ((Comparison) data.get("comparison"));
                    int compareTo = data.getInt("compare_to");
                    return comparison.compare(amount, compareTo);
                }));

        register(new ConditionFactory<>(Apugli.identifier("entity_group"), new SerializableData()
                .add("group", ApugliDataTypes.APUGLI_ENTITY_GROUP),
                (data, entity) ->
                        entity.getGroup() == data.get("group")));
        register(new ConditionFactory<>(Apugli.identifier("can_have_effect"), new SerializableData()
                .add("effect", SerializableDataTypes.STATUS_EFFECT),
                (data, entity) -> {
                    StatusEffect effect = (StatusEffect)data.get("effect");
                    StatusEffectInstance instance = new StatusEffectInstance(effect);
                    return entity.canHaveStatusEffect(instance);
                }));
    }

    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
