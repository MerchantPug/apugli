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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CachedBlockInRadiusCondition implements IConditionFactory<Entity> {

    private static final Map<SerializableData.Instance, ConcurrentHashMap<BlockPos, Boolean>> CACHED_BLOCK_POS_VALUES = new HashMap<>();
    private static final Set<BlockPos> DIRTY = new HashSet<>();

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
        CACHED_BLOCK_POS_VALUES.computeIfAbsent(data, d -> new ConcurrentHashMap<>());
        Comparison comparison = data.get("comparison");
        int compareTo = data.getInt("compare_to");

        int count = 0;
        int stopAt = -1;

        switch (comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> stopAt = compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL -> stopAt = compareTo;
        }

        Collection<BlockPos> posCollection = Shape.getPositions(entity.blockPosition(), data.get("shape"), data.getInt("radius"));

        for (BlockPos pos : CACHED_BLOCK_POS_VALUES.get(data).keySet()) {
            if (DIRTY.contains(pos)) {
                CACHED_BLOCK_POS_VALUES.get(data).remove(pos);
                DIRTY.remove(pos);
            }
        }

        for(BlockPos pos : posCollection) {
            if (count == stopAt) break;
            boolean containsPos = CACHED_BLOCK_POS_VALUES.get(data).containsKey(pos);
            boolean blockCheck = containsPos ? CACHED_BLOCK_POS_VALUES.get(data).get(pos) : Services.CONDITION.checkBlock(data, "block_condition", entity.level, pos);
            if (blockCheck) {
                count++;
            }
            if (!containsPos || CACHED_BLOCK_POS_VALUES.get(data).get(pos) != blockCheck) {
                CACHED_BLOCK_POS_VALUES.get(data).put(pos, blockCheck);
            }
        }

        return comparison.compare(count, compareTo);
    }

    public static void markChunkDirty(LevelAccessor level, ChunkAccess chunk) {
        for (Map.Entry<SerializableData.Instance, ConcurrentHashMap<BlockPos, Boolean>> entry : CACHED_BLOCK_POS_VALUES.entrySet()) {
            for (BlockPos pos : entry.getValue().keySet()) {
                if (level.getChunk(pos) == chunk) {
                    DIRTY.add(pos);
                }
            }
        }
    }

    public static void markDirty(BlockPos pos) {
        DIRTY.add(pos);
    }

    public static void clearCache() {
        CACHED_BLOCK_POS_VALUES.clear();
        DIRTY.clear();
    }
}
