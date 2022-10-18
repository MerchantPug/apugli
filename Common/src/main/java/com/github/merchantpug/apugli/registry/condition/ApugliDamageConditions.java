package com.github.merchantpug.apugli.registry.condition;

import com.github.merchantpug.apugli.condition.factory.IConditionFactory;
import com.github.merchantpug.apugli.condition.factory.damage.IsMagicCondition;
import com.github.merchantpug.apugli.platform.Services;
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
