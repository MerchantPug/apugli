package net.merchantpug.apugli.condition.factory.block;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class IsAirCondition implements IConditionFactory<BlockInWorld> {
    
    @Override
    public boolean check(SerializableData.Instance data, BlockInWorld block) {
        return block.getState().isAir();
    }
    
}
