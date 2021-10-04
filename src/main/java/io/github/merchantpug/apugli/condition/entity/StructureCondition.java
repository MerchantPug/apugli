package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.List;

public class StructureCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (entity.getEntityWorld().isClient()) return false;
        StructureFeature<?> structure = Registry.STRUCTURE_FEATURE.get(data.getId("structure"));
        BlockPos structurePos = ((ServerWorld)entity.getEntityWorld()).locateStructure(structure, entity.getBlockPos(), 12, false);
        if (structurePos != null) {
            ChunkPos structureChunkPos = new ChunkPos(structurePos.getX() >> 4, structurePos.getZ() >> 4);
            StructureStart<?> structureStart = ((ServerWorld)entity.getEntityWorld()).getStructureAccessor().getStructureStart(ChunkSectionPos.from(structureChunkPos, 0), structure, entity.world.getChunk(structurePos));
            List<StructurePiece> structurePieces = structureStart.getChildren().stream().toList();
            for (StructurePiece structurePiece : structurePieces) {
                BlockBox box = structurePiece.getBoundingBox();
                if (entity.getBoundingBox().intersects(Box.from(box))) return true;
            }
        }
        return false;
    }

    public static ConditionFactory<Entity> getFactory() {
        return new ConditionFactory<>(Apugli.identifier("structure"), new SerializableData()
                .add("structure", SerializableDataTypes.IDENTIFIER),
                ApugliEntityGroupCondition::condition
        );
    }
}
