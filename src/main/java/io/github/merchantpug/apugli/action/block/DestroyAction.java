package io.github.merchantpug.apugli.action.block;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.action.entity.SwingHandAction;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
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
