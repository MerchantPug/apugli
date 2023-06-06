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
import net.minecraft.core.Vec3i;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CachedBlockInRadiusCondition implements IConditionFactory<Entity> {

    private static final Map<BlockPos, Map<SerializableData.Instance, Boolean>> CHECKED_BLOCK_POS_CACHE = new ConcurrentHashMap<>();
    private static final Map<BlockPos, Map<SerializableData.Instance, Tuple<Collection<BlockPos>, Boolean>>> FINAL_VALUE_CACHE = new ConcurrentHashMap<>();
    private static final Map<SerializableData.Instance, Map<Entity, Integer>> ENTITIES = new HashMap<>();

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
        int invalidationDistance = data.getInt("radius") * data.getInt("radius") * 2;

        ENTITIES.computeIfAbsent(data, instance -> new ConcurrentHashMap<>());
        if (!ENTITIES.get(data).containsKey(entity)) {
            ENTITIES.get(data).put(entity, 0);
        }

        for (BlockPos pos : CHECKED_BLOCK_POS_CACHE.keySet()) {
            if (ENTITIES.get(data).keySet().stream().allMatch(e -> e.distanceToSqr(pos.getCenter()) >= invalidationDistance)) {
                invalidate(pos, data);
            }
        }
        for (Map.Entry<Entity, Integer> entry : ENTITIES.get(data).entrySet()) {
            if (entry.getValue() > ENTITIES.get(data).size()) {
                ENTITIES.get(data).remove(entry.getKey());
                continue;
            }
            entry.setValue(entry.getValue() + 1);

            if (entry.getKey() == entity) {
                entry.setValue(0);
            }
        }

        return getReturnValue(entity.level, entity.blockPosition(), data);
    }

    public static void invalidate(BlockPos pos, SerializableData.Instance data) {
        if (CHECKED_BLOCK_POS_CACHE.containsKey(pos) && CHECKED_BLOCK_POS_CACHE.get(pos) != null) {
            CHECKED_BLOCK_POS_CACHE.get(pos).remove(data);
            if (CHECKED_BLOCK_POS_CACHE.get(pos).isEmpty()) {
                CHECKED_BLOCK_POS_CACHE.remove(pos);
            }
        }
        for (Map.Entry<BlockPos, Map<SerializableData.Instance, Tuple<Collection<BlockPos>, Boolean>>> entry : FINAL_VALUE_CACHE.entrySet()) {
            if (entry.getValue().containsKey(data) && entry.getValue().get(data).getA().contains(pos)) {
                FINAL_VALUE_CACHE.get(entry.getKey()).remove(data);
                if (FINAL_VALUE_CACHE.get(entry.getKey()).isEmpty()) {
                    FINAL_VALUE_CACHE.remove(entry.getKey());
                }
                break;
            }
        }
    }

    public static void invalidate(BlockPos pos) {
        CHECKED_BLOCK_POS_CACHE.remove(pos);
        for (Map.Entry<BlockPos, Map<SerializableData.Instance, Tuple<Collection<BlockPos>, Boolean>>> entry : FINAL_VALUE_CACHE.entrySet()) {
            for (SerializableData.Instance data : entry.getValue().keySet()) {
                if (entry.getValue().get(data).getA().contains(pos)) {
                    FINAL_VALUE_CACHE.remove(entry.getKey());
                }
            }
        }
    }

    public static void clearCache() {
        CHECKED_BLOCK_POS_CACHE.clear();
        FINAL_VALUE_CACHE.clear();
        ENTITIES.clear();
    }

    public static boolean getReturnValue(Level level, BlockPos center, SerializableData.Instance data) {
        if (FINAL_VALUE_CACHE.containsKey(center) && FINAL_VALUE_CACHE.get(center).containsKey(data)) {
            return FINAL_VALUE_CACHE.get(center).get(data).getB();
        }

        Comparison comparison = data.get("comparison");
        int compareTo = data.getInt("compare_to");
        Shape shape = data.get("shape");
        int radius = data.getInt("radius");
        Collection<BlockPos> collection = new HashSet<>();

        int count = 0;
        int stopAt = -1;

        switch (comparison) {
            case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> stopAt = compareTo + 1;
            case LESS_THAN, GREATER_THAN_OR_EQUAL -> stopAt = compareTo;
        }

        for (int r = 0; r <= radius; r++) {
            if (r == 0) {
                count = incrementCountIfPosCheck(count, center, level, data, collection);
                if (count == stopAt) break;
            } else {
                for (int j = 0; j < 3; j++) {
                    Vec3i direction = j == 0 ? new Vec3i(0,1,0) : j == 1? new Vec3i(1,0,0) : new Vec3i(0,0,1);
                    Vec3i tangent1;
                    Vec3i tangent2;
                    if(j == 0) {
                        tangent1 = new Vec3i(1,0,0);
                        tangent2 = new Vec3i(0,0,1);
                    } else {
                        tangent1 = direction.cross(new Vec3i(0,1,0));
                        tangent2 = new Vec3i(0,1,0);
                    }
                    int offset = (j==0 ? 0 : 1);
                    for (int l1 = -r + offset; l1 <= r; l1++) {
                        for (int l2 = -r + offset; l2 <= r - offset; l2++) {
                            Vec3i p = direction.multiply(r);
                            p = p.offset(tangent1.multiply(l1));
                            p = p.offset(tangent2.multiply(l2));
                            if (
                                    shape == Shape.CUBE
                                    || shape == Shape.CHEBYSHEV
                                    || (shape == Shape.SPHERE || shape == Shape.EUCLIDEAN) && p.getX() * p.getX() + p.getY() * p.getY() + p.getZ() * p.getZ() <= radius * radius
                                    || Math.abs(p.getX()) + Math.abs(p.getY()) + Math.abs(p.getZ()) <= radius
                            ) {
                                BlockPos offsetPos = center.offset(p);
                                count = incrementCountIfPosCheck(count, offsetPos, level, data, collection);
                                if (count == stopAt) break;
                                BlockPos negativeOffsetPos = center.offset(p.multiply(-1));
                                count = incrementCountIfPosCheck(count, negativeOffsetPos, level, data, collection);
                                if (count == stopAt) break;
                            }
                        }
                        if (count == stopAt) break;
                    }
                    if (count == stopAt) break;
                }
            }
        }
        var cache = FINAL_VALUE_CACHE.computeIfAbsent(center, pos -> new ConcurrentHashMap<>());
        int finalCount = count;
        var cacheValue = cache.computeIfAbsent(data, d -> new Tuple<>(collection, comparison.compare(finalCount, compareTo)));
        return cacheValue.getB();
    }

    private static int incrementCountIfPosCheck(int currentCount, BlockPos pos, Level level, SerializableData.Instance data, Collection<BlockPos> collection) {
        var cache = CHECKED_BLOCK_POS_CACHE.computeIfAbsent(pos, p -> new ConcurrentHashMap<>());
        boolean bl = cache.computeIfAbsent(data, d -> Services.CONDITION.checkBlock(d, "block_condition", level, pos));
        collection.add(pos);
        if (bl) {
            currentCount++;
        }
        return currentCount;
    }

}
