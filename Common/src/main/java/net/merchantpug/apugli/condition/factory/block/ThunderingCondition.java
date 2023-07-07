package net.merchantpug.apugli.condition.factory.block;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class ThunderingCondition implements IConditionFactory<BlockInWorld> {

    @Override
    public boolean check(SerializableData.Instance data, BlockInWorld block) {
        if (!(block.getLevel() instanceof Level level)) {
            return false;
        }
        return level.isThundering();
    }

}