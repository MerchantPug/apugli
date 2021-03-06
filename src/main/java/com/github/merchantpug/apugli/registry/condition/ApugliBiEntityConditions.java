package com.github.merchantpug.apugli.registry.condition;

import com.github.merchantpug.apugli.condition.bientity.PrimeAdversaryCondition;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import com.github.merchantpug.apugli.condition.bientity.HitsOnTargetCondition;
import net.minecraft.entity.Entity;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

public class ApugliBiEntityConditions {
    public static void register() {
        register(HitsOnTargetCondition.getFactory());
        register(PrimeAdversaryCondition.getFactory());
    }

    private static void register(ConditionFactory<Pair<Entity, Entity>> conditionFactory) {
        Registry.register(ApoliRegistries.BIENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
