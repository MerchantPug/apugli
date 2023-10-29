package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.integration.pehkui.ApoliScaleModifier;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.UpdateScaleDataPacket;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public interface ModifyScalePowerFactory<P> extends ValueModifyingPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ValueModifyingPowerFactory.getSerializableData()
                .add("scale_type", SerializableDataTypes.IDENTIFIER, null)
                .add("scale_types", SerializableDataType.list(SerializableDataTypes.IDENTIFIER), new ArrayList<>());
    }

    default void tick(P power, LivingEntity entity) {
        // Failsafe to make sure that this won't run without Pehkui.
        if (!Services.PLATFORM.isModLoaded("pehkui")) return;

        ResourceLocation mappedScaleModifierId = getMappedScaleModifierId(power);
        if (getModifierFromCache(mappedScaleModifierId) == null) {
            addModifierToCache(mappedScaleModifierId, new ApoliScaleModifier(getModifiers(power, entity), mappedScaleModifierId));
        }
        ApoliScaleModifier modifier = getModifierFromCache(mappedScaleModifierId);

        boolean addedScales = false;
        boolean removedScales = false;

        double cachedValue = getCachedEntityScale(power, entity);
        double modifiedValue = Services.PLATFORM.applyModifiers(entity, getModifiers(power, entity), 1.0D);
        if (modifiedValue != cachedValue) {
            setCachedEntityScale(power, entity, modifiedValue);
            getScaleTypes(power).stream().map(id -> ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id)).forEach(scaleType -> scaleType.getScaleData(entity).onUpdate());
            Services.PLATFORM.sendS2CTrackingAndSelf(new UpdateScaleDataPacket(entity.getId(), getScaleTypes(power).stream().toList()), entity);
        }

        for (ResourceLocation scaleTypeId : getScaleTypes(power)) {

            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);
            SortedSet<ScaleModifier> modifiers = scaleData.getBaseValueModifiers();

            if (!modifiers.contains(modifier) && Services.POWER.isActive(power, entity)) {
                ((ScaleDataAccess)scaleData).apugli$addToApoliScaleModifiers(mappedScaleModifierId);
                scaleData.getBaseValueModifiers().add(modifier);
                addedScales = true;
            } else if (modifiers.contains(modifier) && !Services.POWER.isActive(power, entity)) {
                ((ScaleDataAccess)scaleData).apugli$removeFromApoliScaleModifiers(mappedScaleModifierId);
                scaleData.getBaseValueModifiers().remove(modifier);
                removedScales = true;
            }

        }

        if (addedScales) {
            Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(entity.getId(), getScaleTypes(power).stream().toList(), mappedScaleModifierId, getModifiers(power, entity), false), entity);
        } else if (removedScales) {
            Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(entity.getId(), getScaleTypes(power).stream().toList(), mappedScaleModifierId, List.of(), true), entity);
        }
    }

    default void onAdded(P power, LivingEntity entity) {
        setCachedEntityScale(power, entity, 1.0F);
    }

    default void onRemoved(P power, LivingEntity entity) {
        ResourceLocation mappedScaleModifierId = getMappedScaleModifierId(power);

        ApoliScaleModifier modifier = getModifierFromCache(mappedScaleModifierId);

        for (ResourceLocation scaleTypeId : getScaleTypes(power)) {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);

            ((ScaleDataAccess)scaleData).apugli$removeFromApoliScaleModifiers(mappedScaleModifierId);
            scaleData.getBaseValueModifiers().remove(modifier);

            Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(entity.getId(), getScaleTypes(power).stream().toList(), mappedScaleModifierId, List.of(), true), entity);
        }
        removeCachedEntityScale(power, entity);
    }

    default void clearFromAll() {
        for (Entity entity : getEntitiesWithPower()) {
            if (entity instanceof LivingEntity living && !Services.POWER.getPowers(living, this, true).isEmpty()) {
                Services.POWER.getPowers(living, this, true).forEach(p -> onRemoved(p, living));
            }
        }
    }

    @Nullable ApoliScaleModifier getModifierFromCache(ResourceLocation id);

    void addModifierToCache(ResourceLocation id, ApoliScaleModifier modifier);

    void clearModifiersFromCache();

    Set<ResourceLocation> getScaleTypes(P power);

    void clearScaleTypeCache();
    double getCachedEntityScale(P power, Entity entity);
    void setCachedEntityScale(P power, Entity entity, double value);
    void removeCachedEntityScale(P power, Entity entity);
    Set<Entity> getEntitiesWithPower();

    ResourceLocation getPowerId(P power);

    default ResourceLocation getMappedScaleModifierId(P power) {
        return Apugli.asResource("modifyscalepower/" + getPowerId(power).getNamespace() + "/" + getPowerId(power).getPath());
    }

}
