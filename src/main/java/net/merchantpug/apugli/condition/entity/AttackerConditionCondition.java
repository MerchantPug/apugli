package net.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.function.Predicate;

public class AttackerConditionCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        Predicate<Pair<Entity, Entity>> pair = data.get("bientity_condition");
        if (entity instanceof LivingEntity living) {
            Entity attacker = ApugliEntityComponents.ATTACK_COMPONENT.get(entity).getAttacker();
            if (attacker != null) {
                return pair.test(new Pair<>(living, attacker));
            }
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("attacker_condition"), new SerializableData()
                .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION),
                AttackerConditionCondition::condition
        );
    }
}
