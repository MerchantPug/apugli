package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public class PreventEntitySelectionPower extends Power {
    public final Predicate<Entity> entityCondition;
    public final Predicate<Tuple<Entity, Entity>> biEntityCondition;

    public PreventEntitySelectionPower(PowerType<?> type, LivingEntity entity,
                                       Predicate<Entity> entityCondition,
                                       Predicate<Tuple<Entity, Entity>> biEntityCondition) {
        super(type, entity);
        this.entityCondition = entityCondition;
        this.biEntityCondition = biEntityCondition;
    }

    public boolean shouldPrevent(Entity entity) {
        return (entityCondition == null || entityCondition.test(entity)) && (biEntityCondition == null || biEntityCondition.test(new Tuple<>(this.entity, entity)));
    }

    public static class Factory extends SimplePowerFactory<PreventEntitySelectionPower> {

        public Factory() {
            super("prevent_entity_selection",
                    new SerializableData()
                            .add("entity_condition", Services.CONDITION.entityDataType(), null)
                            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null),
                    data -> (type, entity) -> new PreventEntitySelectionPower(type, entity,
                            Services.CONDITION.entityPredicate(data, "entity_condition"),
                            Services.CONDITION.biEntityPredicate(data, "bientity_condition")));
            allowCondition();
        }

        @Override
        public Class<PreventEntitySelectionPower> getPowerClass() {
            return PreventEntitySelectionPower.class;
        }

    }

}
