package io.github.merchantpug.apugli.action.block;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

public class DestroyAction {
    public static void action(SerializableData.Instance data, Triple<World, BlockPos, Direction> block) {
        if (block.getLeft().isClient()) return;
        block.getLeft().breakBlock(block.getMiddle(), data.getBoolean("drop_block"));
    }

    public static ActionFactory<Triple<World, BlockPos, Direction>> getFactory() {
        return new ActionFactory<>(Apugli.identifier("destroy"), new SerializableData()
                .add("drop_block", SerializableDataTypes.BOOLEAN),
                DestroyAction::action
        );
    }
}
