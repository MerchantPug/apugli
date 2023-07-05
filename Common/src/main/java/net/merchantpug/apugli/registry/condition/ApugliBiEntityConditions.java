package net.merchantpug.apugli.registry.condition;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.condition.factory.bientity.HitsOnTargetCondition;
import net.merchantpug.apugli.condition.factory.bientity.PrimeAdversaryCondition;
import net.merchantpug.apugli.condition.factory.bientity.ProjectileOwnerCondition;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

public class ApugliBiEntityConditions {
    
    public static void registerAll() {
        register("hits_on_target",  new HitsOnTargetCondition());
        register("prime_adversary", new PrimeAdversaryCondition());
        register("projectile_owner", new ProjectileOwnerCondition());
    }
    
    private static void register(String name, IConditionFactory<Tuple<Entity, Entity>> factory) {
        Services.CONDITION.registerBiEntity(name, factory);
    }
    
}
