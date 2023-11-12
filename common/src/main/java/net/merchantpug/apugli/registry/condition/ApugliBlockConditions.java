package net.merchantpug.apugli.registry.condition;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.condition.factory.block.InRainCondition;
import net.merchantpug.apugli.condition.factory.block.IsAirCondition;
import net.merchantpug.apugli.condition.factory.block.RainingCondition;
import net.merchantpug.apugli.condition.factory.block.ThunderingCondition;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class ApugliBlockConditions {
    
    public static void registerAll() {
        register("air", new IsAirCondition());
        register("in_rain", new InRainCondition());
        register("raining", new RainingCondition());
        register("thundering", new ThunderingCondition());
    }
    
    private static void register(String name, IConditionFactory<BlockInWorld> factory) {
        Services.CONDITION.registerBlock(name, factory);
    }
    
}
