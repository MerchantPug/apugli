package net.merchantpug.apugli.action.factory.block;

import net.merchantpug.apugli.action.factory.IActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

public class DestroyAction implements IActionFactory<Triple<Level, BlockPos, Direction>> {
    
    @Override
    public SerializableData getSerializableData() {
        return  new SerializableData()
            .add("drop_block", SerializableDataTypes.BOOLEAN);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Triple<Level, BlockPos, Direction> block) {
        if(block.getLeft().isClientSide()) return;
        block.getLeft().destroyBlock(block.getMiddle(), data.getBoolean("drop_block"));
    }
    
}
