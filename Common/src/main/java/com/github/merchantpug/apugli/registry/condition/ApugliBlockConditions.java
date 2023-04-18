package com.github.merchantpug.apugli.registry.condition;

import com.github.merchantpug.apugli.condition.factory.IConditionFactory;
import com.github.merchantpug.apugli.condition.factory.block.IsAirCondition;
import com.github.merchantpug.apugli.platform.Services;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class ApugliBlockConditions {
    
    public static void registerAll() {
        register("air", new IsAirCondition());
    }
    
    private static void register(String name, IConditionFactory<BlockInWorld> factory) {
        Services.CONDITION.registerBlock(name, factory);
    }
    
}
