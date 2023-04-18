package com.github.merchantpug.apugli.registry.condition;

import com.github.merchantpug.apugli.condition.factory.IConditionFactory;
import com.github.merchantpug.apugli.condition.factory.bientity.HitsOnTargetCondition;
import com.github.merchantpug.apugli.condition.factory.bientity.PrimeAdversaryCondition;
import com.github.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class ApugliBiEntityConditions {
    
    public static void registerAll() {
        register("hits_on_target",  new HitsOnTargetCondition());
        register("prime_adversary", new PrimeAdversaryCondition());
    }
    
    private static void register(String name, IConditionFactory<Tuple<Entity, Entity>> factory) {
        Services.CONDITION.registerBiEntity(name, factory);
    }
    
}
