package com.github.merchantpug.apugli.power;

import the.great.migration.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import java.util.function.Predicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PreventLabelRenderPower extends Power {
    private final Predicate<Entity> entityCondition;
    private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

    public PreventLabelRenderPower(PowerType<?> type, LivingEntity player, Predicate<Entity> entityPredicate, Predicate<Tuple<Entity, Entity>> biEntityPredicate) {
        super(type, player);
        this.entityCondition = entityPredicate;
        this.biEntityCondition = biEntityPredicate;
    }

    public boolean shouldHide(LivingEntity living) {
        return (this.entityCondition == null || this.entityCondition.test(living)) && (this.biEntityCondition == null || this.biEntityCondition.test(new Tuple<>(entity, living)));
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<PreventLabelRenderPower>(
                Apugli.identifier("prevent_label_render"),
                new SerializableData()
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("bientity_condition", Services.CONDITION.biEntityDataType(), null),
                data -> (type, entity) -> new PreventLabelRenderPower(type, entity, (Predicate<Entity>) data.get("entity_condition"), (Predicate<Tuple<Entity, Entity>>) data.get("bientity_condition")))
                .allowCondition();
    }
}