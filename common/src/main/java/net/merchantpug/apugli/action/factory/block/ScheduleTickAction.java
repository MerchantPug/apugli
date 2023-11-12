package net.merchantpug.apugli.action.factory.block;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

public class ScheduleTickAction implements IActionFactory<Triple<Level, BlockPos, Direction>> {

    @Override
    public SerializableData getSerializableData() {
        return  new SerializableData()
                .add("min", SerializableDataTypes.INT)
                .add("max", SerializableDataTypes.INT);
    }

    @Override
    public void execute(SerializableData.Instance data, Triple<Level, BlockPos, Direction> block) {
        block.getLeft().scheduleTick(block.getMiddle(), block.getLeft().getBlockState(block.getMiddle()).getBlock(), Mth.nextInt(block.getLeft().getRandom(), data.getInt("min"), data.getInt("max")));
    }

}