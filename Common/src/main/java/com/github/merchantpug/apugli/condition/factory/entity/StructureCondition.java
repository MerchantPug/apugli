package com.github.merchantpug.apugli.condition.factory.entity;

import com.github.merchantpug.apugli.condition.factory.IConditionFactory;
import com.mojang.datafixers.util.Pair;
import com.github.merchantpug.apugli.util.ApugliDataTypes;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class StructureCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("structure", ApugliDataTypes.STRUCTURE, null)
            .add("tag", ApugliDataTypes.STRUCTURE_TAG, null);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        if(!(entity.level instanceof ServerLevel level)) return false;
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
        HolderSet<Structure> holders = null;
        ResourceKey<Structure> structure = data.get("structure");
        TagKey<Structure> tag = data.get("tag");
        if(structure != null) {
            var entry = registry.getHolder(structure);
            if(entry.isPresent()) {
                holders = HolderSet.direct(entry.get());
            }
        } else  {
            var optionalList = registry.getTag(tag);
            if(optionalList.isPresent()) {
                holders = optionalList.get();
            }
        }
        if(holders == null) return false;
        Pair<BlockPos, Holder<Structure>> result = level.getChunkSource().getGenerator().findNearestMapStructure(level, holders, entity.blockPosition(), 10, false);
        if(result != null) {
            ChunkPos structureChunkPos = new ChunkPos(result.getFirst().getX() >> 4, result.getFirst().getZ() >> 4);
            StructureStart structureStart = level.structureManager().getStartForStructure(
                SectionPos.of(structureChunkPos, 0),
                result.getSecond().value(),
                level.getChunk(result.getFirst())
            );
            List<StructurePiece> structurePieces = structureStart.getPieces().stream().toList();
            for(StructurePiece structurePiece : structurePieces) {
                BoundingBox box = structurePiece.getBoundingBox();
                if(entity.getBoundingBox().intersects(AABB.of(box))) return true;
            }
        }
        return false;
    }

}
