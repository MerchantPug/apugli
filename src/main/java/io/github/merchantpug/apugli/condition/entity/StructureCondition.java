package io.github.merchantpug.apugli.condition.entity;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.List;

public class StructureCondition {
    public static boolean condition(SerializableData.Instance data, Entity entity) {
        if (entity.getEntityWorld().isClient()) return false;
        ServerWorld world = (ServerWorld)entity.getWorld();
        Registry<ConfiguredStructureFeature<?, ?>> registry = world.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);

        RegistryEntryList<ConfiguredStructureFeature<?, ?>> entryList = null;
        RegistryKey<ConfiguredStructureFeature<?, ?>> structure = data.get("structure");
        TagKey<ConfiguredStructureFeature<?, ?>> tag = data.get("structure_tag");
        if(structure != null) {
            var entry = registry.getEntry(structure);
            if(entry.isPresent()) {
                entryList = RegistryEntryList.of(entry.get());
            }
        }
        if(entryList == null) {
            var optionalList = registry.getEntryList(tag);
            if(optionalList.isPresent()) {
                entryList = optionalList.get();
            }
        }
        com.mojang.datafixers.util.Pair<BlockPos, RegistryEntry<ConfiguredStructureFeature<?, ?>>> result = world.getChunkManager().getChunkGenerator().locateStructure(world, entryList, entity.getBlockPos(), 10, false);
        if (result != null) {
            ChunkPos structureChunkPos = new ChunkPos(result.getFirst().getX() >> 4, result.getFirst().getZ() >> 4);
            StructureStart structureStart = ((ServerWorld)entity.getEntityWorld()).getStructureAccessor().getStructureStart(ChunkSectionPos.from(structureChunkPos, 0), result.getSecond().value(), entity.world.getChunk(result.getFirst()));
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
                .add("structure", ApugliDataTypes.STRUCTURE, null)
                .add("tag", ApugliDataTypes.STRUCTURE_TAG, null),
                StructureCondition::condition
        );
    }
}
