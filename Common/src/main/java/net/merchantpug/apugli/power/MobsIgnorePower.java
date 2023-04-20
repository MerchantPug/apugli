package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import java.util.function.Predicate;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MobsIgnorePower extends Power {
    private final Predicate<Entity> mobCondition;
    private final Predicate<Tuple<Entity, Entity>> biEntityCondition;

    public MobsIgnorePower(PowerType<?> type, LivingEntity player, Predicate<Entity> mobCondition, Predicate<Tuple<Entity, Entity>> biEntityCondition) {
        super(type, player);
        this.mobCondition = mobCondition;
        this.biEntityCondition = biEntityCondition;
    }

    public boolean shouldIgnore(Entity mob) {
        return (this.mobCondition == null || this.mobCondition.test(mob)) && (this.biEntityCondition == null || this.biEntityCondition.test(new Tuple<>(entity, mob)));
    }

    public static class Factory extends SimplePowerFactory<MobsIgnorePower> {

        public Factory() {
            super("mobs_ignore",
                    new SerializableData()
                            .add("mob_condition", Services.CONDITION.entityDataType(), null)
                            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null),
                    data -> (type, player) -> new MobsIgnorePower(type, player, Services.CONDITION.entityPredicate(data, "mob_condition"), Services.CONDITION.biEntityPredicate(data, "bientity_condition"));

            allowCondition();
        }

        @Override
        public Class<MobsIgnorePower> getPowerClass() {
            return MobsIgnorePower.class;
        }

    }

}