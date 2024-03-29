package net.merchantpug.apugli.registry.condition;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.condition.factory.bientity.CompareDimensionsCondition;
import net.merchantpug.apugli.condition.factory.bientity.HitsOnTargetCondition;
import net.merchantpug.apugli.condition.factory.bientity.OwnerCondition;
import net.merchantpug.apugli.condition.factory.bientity.PrimeAdversaryCondition;
import net.merchantpug.apugli.condition.factory.bientity.integration.pehkui.CompareScalesCondition;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class ApugliBiEntityConditions {
    
    public static void registerAll() {
        register("compare_dimensions", new CompareDimensionsCondition());
        register("hits_on_target",  new HitsOnTargetCondition());
        register("owner", new OwnerCondition());
        register("prime_adversary", new PrimeAdversaryCondition());

        register("compare_scales", new CompareScalesCondition());
    }
    
    private static void register(String name, IConditionFactory<Tuple<Entity, Entity>> factory) {
        Services.CONDITION.registerBiEntity(name, factory);
    }
    
}
