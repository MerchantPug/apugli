package com.github.merchantpug.apugli.condition;

import com.github.merchantpug.apugli.condition.configuration.FabricConditionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityCondition;
import net.minecraft.world.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class FabricEntityCondition extends EntityCondition<FabricConditionConfiguration<Entity>> {
    
    public FabricEntityCondition(SerializableData data, BiPredicate<SerializableData.Instance, Entity> condition) {
        super(FabricConditionConfiguration.codec(data, condition));
    }
    
    @Override
    protected boolean check(FabricConditionConfiguration<Entity> config, Entity entity) {
        return config.condition().test(entity);
    }
    
}
