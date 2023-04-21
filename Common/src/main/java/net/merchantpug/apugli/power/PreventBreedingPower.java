package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventBreedingPower extends Power {
    private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
    private final Consumer<Tuple<Entity, Entity>> biEntityAction;
    public final boolean preventFollow;

    public PreventBreedingPower(PowerType<?> type, LivingEntity entity, Predicate<Tuple<Entity, Entity>> biEntityCondition, Consumer<Tuple<Entity, Entity>> biEntityAction, boolean preventFollow) {
        super(type, entity);
        this.biEntityCondition = biEntityCondition;
        this.biEntityAction = biEntityAction;
        this.preventFollow = preventFollow;
    }

    public boolean doesApply(Entity mobEntity) {
        return this.biEntityCondition == null || this.biEntityCondition.test(new Tuple<>(entity, mobEntity));
    }

    public void executeAction(Entity mobEntity) {
        if(biEntityAction == null) return;
        this.biEntityAction.accept(new Tuple<>(entity, mobEntity));
    }

    public boolean hasAction() {
        return biEntityAction != null;
    }

    public static class Factory extends SimplePowerFactory<PreventBreedingPower> {

        public Factory() {
            super("prevent_breeding_power",
                    new SerializableData()
                            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                            .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                            .add("prevent_follow", SerializableDataTypes.BOOLEAN, true),
                    data -> (type, entity) -> new PreventBreedingPower(type, entity, data.get("bientity_condition"), data.get("bientity_action"), data.getBoolean("prevent_follow")));
        }

        @Override
        public Class<PreventBreedingPower> getPowerClass() {
            return PreventBreedingPower.class;
        }
    }

}
