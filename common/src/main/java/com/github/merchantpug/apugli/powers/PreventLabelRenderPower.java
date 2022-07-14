package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Predicate;

public class PreventLabelRenderPower extends Power {
    private final Predicate<LivingEntity> entityCondition;

    public PreventLabelRenderPower(PowerType<?> type, PlayerEntity player, Predicate<LivingEntity> entityPredicate) {
        super(type, player);
        this.entityCondition = entityPredicate;
    }

    public boolean shouldHide(LivingEntity living) {
        return this.entityCondition == null || this.entityCondition.test(living);
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<PreventLabelRenderPower>(
                Apugli.identifier("prevent_label_render"),
                new SerializableData()
                        .add("entity_condition", SerializableDataType.ENTITY_CONDITION, null),
                data -> (type, entity) -> new PreventLabelRenderPower(type, entity, (Predicate<LivingEntity>) data.get("entity_condition")))
                .allowCondition();
    }
}