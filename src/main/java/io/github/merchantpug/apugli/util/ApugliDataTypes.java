package io.github.merchantpug.apugli.util;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.mixin.AbstractDustParticleEffectAccessor;
import io.github.merchantpug.apugli.registry.ApugliEntityGroups;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EntityGroup;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.Vec3f;

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
    public static final SerializableDataType<AbstractDustParticleEffect> DUST_PARTICLE = SerializableDataType.compound(AbstractDustParticleEffect.class,
            new SerializableData()
                    .add("color_transition", SerializableDataTypes.BOOLEAN, false)
                    .add("red", SerializableDataTypes.FLOAT,1.0F)
                    .add("green", SerializableDataTypes.FLOAT,1.0F)
                    .add("blue", SerializableDataTypes.FLOAT,1.0F)
                    .add("scale", SerializableDataTypes.FLOAT,1.0F)
                    .add("red_to", SerializableDataTypes.FLOAT,0.0F)
                    .add("green_to", SerializableDataTypes.FLOAT,0.0F)
                    .add("blue_to", SerializableDataTypes.FLOAT,0.0F),
            (data) -> {
                if (data.getBoolean("color_transition")) return new DustColorTransitionParticleEffect(new Vec3f(data.getFloat("red"), data.getFloat("green"), data.getFloat("blue")), new Vec3f(data.getFloat("red_to"), data.getFloat("green_to"), data.getFloat("blue_to")), data.getFloat("scale"));
                return new DustParticleEffect(new Vec3f(data.getFloat("red"), data.getFloat("green"), data.getFloat("blue")), data.getFloat("scale"));
            },
            ((serializableData, particleEffect) -> {
                SerializableData.Instance data = serializableData.new Instance();
                data.set("color_transition", particleEffect instanceof DustColorTransitionParticleEffect);
                data.set("red", ((AbstractDustParticleEffectAccessor)particleEffect).getColor().getX());
                data.set("green", ((AbstractDustParticleEffectAccessor)particleEffect).getColor().getY());
                data.set("blue", ((AbstractDustParticleEffectAccessor)particleEffect).getColor().getZ());
                data.set("scale", ((AbstractDustParticleEffectAccessor)particleEffect).getScale());
                data.set("red_to", particleEffect instanceof DustColorTransitionParticleEffect ? ((DustColorTransitionParticleEffect)particleEffect).getToColor().getX() : 0.0F);
                data.set("green_to", particleEffect instanceof DustColorTransitionParticleEffect ? ((DustColorTransitionParticleEffect)particleEffect).getToColor().getY() : 0.0F);
                data.set("blue_to", particleEffect instanceof DustColorTransitionParticleEffect ? ((DustColorTransitionParticleEffect)particleEffect).getToColor().getZ() : 0.0F);
                return data;
            }));
}
