package net.merchantpug.apugli.power;

import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
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

    public static class Factory extends SimplePowerFactory<PreventLabelRenderPower> {

        public Factory() {
            super("prevent_label_render",
                    new SerializableData()
                            .add("entity_condition", Services.CONDITION.entityDataType(), null)
                            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null),
                    data -> (type, entity) -> new PreventLabelRenderPower(type, entity, data.get("entity_condition"), data.get("bientity_condition")));
        }

        @Override
        public Class<PreventLabelRenderPower> getPowerClass() {
            return PreventLabelRenderPower.class;
        }

    }

}