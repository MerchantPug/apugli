package com.github.merchantpug.apugli.condition;

import com.github.merchantpug.apugli.condition.configuration.FabricConditionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityCondition;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class FabricBiEntityCondition extends BiEntityCondition<FabricConditionConfiguration<Tuple<Entity, Entity>>> {
    
    public FabricBiEntityCondition(SerializableData data, BiPredicate<SerializableData.Instance, Tuple<Entity, Entity>> condition) {
        super(FabricConditionConfiguration.codec(data, condition));
    }
    
    @Override
    protected boolean check(FabricConditionConfiguration<Tuple<Entity, Entity>> config, Entity actor, Entity target) {
        return config.condition().test(new Tuple<>(actor, target));
    }
    
}
