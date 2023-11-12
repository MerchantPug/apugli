package net.merchantpug.apugli.action;

import net.merchantpug.apugli.action.configuration.FabricActionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.BiEntityAction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
public class FabricBiEntityAction extends BiEntityAction<FabricActionConfiguration<Tuple<Entity, Entity>>> {
    
    public FabricBiEntityAction(SerializableData data, BiConsumer<SerializableData.Instance, Tuple<Entity, Entity>> action) {
        super(FabricActionConfiguration.codec(data, action));
    }
    
    @Override
    public void execute(FabricActionConfiguration<Tuple<Entity, Entity>> config, Entity actor, Entity target) {
        config.action().accept(new Tuple<>(actor, target));
    }
    
}
