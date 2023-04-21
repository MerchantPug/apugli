package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.function.Predicate;

public interface ModifySoulSpeedPowerFactory<P> extends ValueModifyingPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("block_condition", Services.CONDITION.blockDataType(), null);
    }

}
