package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.apoli.util.Shape;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CachedBlockInRadiusCondition implements IConditionFactory<Entity> {

    private static final Map<BlockPos, ConcurrentHashMap<SerializableData.Instance, Boolean>> CACHED_BLOCK_POS_VALUES = new HashMap<>();

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("block_condition", Services.CONDITION.blockDataType(), null)
                .add("radius", SerializableDataTypes.INT)
                .add("shape", SerializableDataType.enumValue(Shape.class), Shape.CUBE)
                .add("compare_to", SerializableDataTypes.INT, 1)
                .add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        Comparison comparison = data.get("comparison");
        int compareTo = data.getInt("compare_to");

        int count = 0;
        int stopAt = -1;

        switch (comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> stopAt = compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL -> stopAt = compareTo;
        }

        Collection<BlockPos> posCollection = Shape.getPositions(entity.blockPosition(), data.get("shape"), data.getInt("radius"));

        for(BlockPos pos : posCollection) {
            if (count == stopAt) break;
            ConcurrentHashMap<SerializableData.Instance, Boolean> map = CACHED_BLOCK_POS_VALUES.computeIfAbsent(pos, p -> new ConcurrentHashMap<>());
            boolean blockCheck = map.computeIfAbsent(data, d ->  Services.CONDITION.checkBlock(data, "block_condition", entity.level, pos));
            if (blockCheck) {
                count++;
            }
        }

        return comparison.compare(count, compareTo);
    }

    public static void invalidateChunk(ChunkAccess chunk) {
        for (int x = chunk.getPos().getMinBlockX(); x < chunk.getPos().getMaxBlockX(); ++x) {
            for (int z = chunk.getPos().getMinBlockZ(); z < chunk.getPos().getMaxBlockZ(); ++z) {
                int finalX = x;
                int finalZ = z;
                for (BlockPos pos : CACHED_BLOCK_POS_VALUES.keySet().stream().filter(pos -> pos.getX() == finalX && pos.getZ() == finalZ).toList()) {
                    invalidate(pos);
                }
            }
        }
    }

    public static void invalidate(BlockPos pos) {
        CACHED_BLOCK_POS_VALUES.remove(pos);
    }

    public static void clearCache() {
        CACHED_BLOCK_POS_VALUES.clear();
    }
}
