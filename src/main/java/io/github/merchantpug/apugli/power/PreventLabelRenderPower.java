package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;

import java.util.function.Predicate;

public class PreventLabelRenderPower extends Power {
    private final Predicate<LivingEntity> entityCondition;

    public PreventLabelRenderPower(PowerType<?> type, LivingEntity player, Predicate<LivingEntity> entityPredicate) {
        super(type, player);
        this.entityCondition = entityPredicate;
    }

    public boolean shouldHide(LivingEntity entity) {
        return this.entityCondition == null || this.entityCondition.test(entity);
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<PreventLabelRenderPower>(
                Apugli.identifier("prevent_label_render"),
                new SerializableData()
                        .add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null),
                data -> (type, player) -> new PreventLabelRenderPower(type, player, (Predicate<LivingEntity>) data.get("entity_condition")))
                .allowCondition();
    }
}