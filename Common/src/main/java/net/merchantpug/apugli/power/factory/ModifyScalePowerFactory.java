package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.integration.pehkui.LerpedApoliScaleModifier;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

public interface ModifyScalePowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("scale_type", SerializableDataTypes.IDENTIFIER, null)
                .add("scale_types", SerializableDataType.list(SerializableDataTypes.IDENTIFIER), new ArrayList<>())
                .add("delay", SerializableDataTypes.INT, null);
    }

    default void tick(P power, LivingEntity entity) {
        // Failsafe to make sure that this won't run without Pehkui.
        if (!Services.PLATFORM.isModLoaded("pehkui")) return;

        ResourceLocation mappedScaleModifierId = getMappedScaleModifierId(power);
        ApoliScaleModifier<P> modifier = getModifierFromCache(mappedScaleModifierId, entity);

        if (modifier != null)
            modifier.tick(entity);

    }

    default void onAdded(P power, LivingEntity entity) {
        ResourceLocation mappedScaleModifierId = getMappedScaleModifierId(power);
        SerializableData.Instance data = getDataFromPower(power);
        if (getModifierFromCache(mappedScaleModifierId, entity) == null) {
            if (data.isPresent("delay")) {
                addModifierToCache(mappedScaleModifierId, entity, new LerpedApoliScaleModifier<>(power, getModifiers(power, entity), mappedScaleModifierId, data.getInt("delay"), Optional.of(1.0F)));
            } else {
                addModifierToCache(mappedScaleModifierId, entity, new ApoliScaleModifier<>(power, getModifiers(power, entity), mappedScaleModifierId));
            }
        }
    }

    default void onRemoved(P power, LivingEntity entity) {
        ResourceLocation mappedScaleModifierId = getMappedScaleModifierId(power);
        ApoliScaleModifier<P> modifier = getModifierFromCache(mappedScaleModifierId, entity);

        for (ResourceLocation scaleTypeId : getScaleTypeCache(power, entity)) {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);

            ((ScaleDataAccess)scaleData).apugli$removeFromApoliScaleModifiers(mappedScaleModifierId);
            scaleData.getBaseValueModifiers().remove(modifier);
        }
        Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(entity.getId(), getScaleTypeCache(power, entity).stream().toList(), mappedScaleModifierId, true), entity);
        removeModifierFromCache(mappedScaleModifierId, entity);
        removeScaleTypesFromCache(power, entity);
    }

    @Nullable ApoliScaleModifier<P> getModifierFromCache(ResourceLocation id, Entity entity);

    void addModifierToCache(ResourceLocation id, Entity entity, ApoliScaleModifier<P> modifier);
    void removeModifierFromCache(ResourceLocation id, Entity entity);
    Set<ResourceLocation> getScaleTypeCache(P power, Entity entity);
    void removeScaleTypesFromCache(P power, Entity entity);

    ResourceLocation getPowerId(P power);

    default ResourceLocation getMappedScaleModifierId(P power) {
        return Apugli.asResource("modifyscalepower/" + getPowerId(power).getNamespace() + "/" + getPowerId(power).getPath());
    }

}
