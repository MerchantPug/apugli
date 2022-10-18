package com.github.merchantpug.apugli.action;

import com.github.merchantpug.apugli.action.configuration.FabricActionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockAction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
public class FabricBlockAction extends BlockAction<FabricActionConfiguration<Triple<Level, BlockPos, Direction>>> {
    
    public FabricBlockAction(SerializableData data, BiConsumer<SerializableData.Instance, Triple<Level, BlockPos, Direction>> action) {
        super(FabricActionConfiguration.codec(data, action));
    }
    
    @Override
    public void execute(FabricActionConfiguration<Triple<Level, BlockPos, Direction>> config, Level level, BlockPos blockPos, Direction direction) {
        config.action().accept(Triple.of(level, blockPos, direction));
    }
    
}
