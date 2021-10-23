package io.github.merchantpug.apugli.util;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ApugliDataTypes {
    public static final SerializableDataType<EntityGroup> APUGLI_ENTITY_GROUP =
            SerializableDataType.mapped(EntityGroup.class, HashBiMap.create(ImmutableMap.of(
                    "smiteable", ApugliEntityGroups.SMITEABLE,
                    "player_undead", ApugliEntityGroups.PLAYER_UNDEAD
            )));
    public static final SerializableDataType<PlayerModelType> PLAYER_MODEL_TYPE =
            SerializableDataType.enumValue(PlayerModelType.class);
    public static final SerializableDataType<BipedEntityModel.ArmPose> ARM_POSE =
            SerializableDataType.enumValue(BipedEntityModel.ArmPose.class);
}
