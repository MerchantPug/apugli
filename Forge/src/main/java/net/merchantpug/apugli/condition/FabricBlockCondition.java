package net.merchantpug.apugli.condition;

import net.merchantpug.apugli.condition.configuration.FabricConditionConfiguration;
import io.github.apace100.apoli.util.SavedBlockPosition;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class FabricBlockCondition extends BlockCondition<FabricConditionConfiguration<BlockInWorld>> {
    
    public FabricBlockCondition(SerializableData data, BiPredicate<SerializableData.Instance, BlockInWorld> condition) {
        super(FabricConditionConfiguration.codec(data, condition));
    }
    
    @Override
    protected boolean check(FabricConditionConfiguration<BlockInWorld> config, LevelReader reader, BlockPos position, NonNullSupplier<BlockState> stateGetter) {
        return config.condition().test(new SavedBlockPosition(reader, position));
    }
    
}
