package net.merchantpug.apugli.registry.condition;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.condition.factory.damage.IsMagicCondition;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;

public class ApugliDamageConditions {
    
    public static void registerAll() {
        register("magic", new IsMagicCondition());
    }
    
    private static void register(String name, IConditionFactory<Tuple<DamageSource, Float>> factory) {
        Services.CONDITION.registerDamage(name, factory);
    }
    
}
