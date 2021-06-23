package io.github.merchantpug.apugli.util;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import net.minecraft.entity.EntityGroup;
import net.minecraft.world.explosion.Explosion;

public class ApugliDataTypes {
    public static final SerializableDataType<EntityGroup> APUGLI_ENTITY_GROUP =
            SerializableDataType.mapped(EntityGroup.class, HashBiMap.create(ImmutableMap.of(
                    "smiteable", ApugliEntityGroups.SMITEABLE,
                    "player_undead", ApugliEntityGroups.PLAYER_UNDEAD
            )));
    public static final SerializableDataType<Explosion.DestructionType> EXPLOSION_BEHAVIOR =
            SerializableDataType.mapped(Explosion.DestructionType.class, HashBiMap.create(ImmutableMap.of(
                    "none", Explosion.DestructionType.NONE,
                    "break", Explosion.DestructionType.BREAK,
                    "destroy", Explosion.DestructionType.DESTROY
            )));
}
