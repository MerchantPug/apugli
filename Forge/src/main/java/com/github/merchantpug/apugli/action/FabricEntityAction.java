package com.github.merchantpug.apugli.action;

import com.github.merchantpug.apugli.action.configuration.FabricActionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import net.minecraft.world.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
public class FabricEntityAction extends EntityAction<FabricActionConfiguration<Entity>> {
    
    public FabricEntityAction(SerializableData data, BiConsumer<SerializableData.Instance, Entity> action) {
        super(FabricActionConfiguration.codec(data, action));
    }
    
    @Override
    public void execute(FabricActionConfiguration<Entity> config, Entity entity) {
        config.action().accept(entity);
    }
    
}
