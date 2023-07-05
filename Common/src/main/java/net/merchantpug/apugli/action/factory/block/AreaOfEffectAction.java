package net.merchantpug.apugli.action.factory.block;

import io.github.apace100.apoli.util.Shape;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

public class AreaOfEffectAction implements IActionFactory<Triple<Level, BlockPos, Direction>> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("block_action", Services.ACTION.blockDataType())
                .add("block_condition", Services.CONDITION.blockDataType(), null)
                .add("radius", SerializableDataTypes.INT, 16)
                .add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE);
    }

    @Override
    public void execute(SerializableData.Instance data, Triple<Level, BlockPos, Direction> block) {

        Level level = block.getLeft();
        BlockPos blockPos = block.getMiddle();
        Direction direction = block.getRight();

        int radius = data.get("radius");

        Shape shape = data.get("shape");

        for (BlockPos collectedBlockPos : Shape.getPositions(blockPos, shape, radius)) {
            if (!Services.CONDITION.checkBlock(data, "block_condition", level, collectedBlockPos)) continue;
            Services.ACTION.executeBlock(data, "block_action", level, collectedBlockPos, direction);
        }

    }
}
