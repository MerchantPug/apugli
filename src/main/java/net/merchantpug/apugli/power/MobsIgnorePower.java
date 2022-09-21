package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.function.Predicate;

public class MobsIgnorePower extends Power {
    private final Predicate<Entity> mobCondition;
    private final Predicate<Pair<Entity, Entity>> biEntityCondition;

    public MobsIgnorePower(PowerType<?> type, LivingEntity player, Predicate<Entity> mobCondition, Predicate<Pair<Entity, Entity>> biEntityCondition) {
        super(type, player);
        this.mobCondition = mobCondition;
        this.biEntityCondition = biEntityCondition;
    }

    public boolean shouldIgnore(Entity mob) {
        return (this.mobCondition == null || this.mobCondition.test(mob)) && (this.biEntityCondition == null || this.biEntityCondition.test(new Pair<>(entity, mob)));
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<MobsIgnorePower>(
                Apugli.identifier("mobs_ignore"),
                new SerializableData()
                        .add("mob_condition", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
                data -> (type, entity) -> new MobsIgnorePower(type, entity, data.get("mob_condition"), data.get("bientity_condition")))
                .allowCondition();
    }
}