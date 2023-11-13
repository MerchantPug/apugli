package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.net.IDN;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApoliScaleModifier<P> extends ScaleModifier {

    protected final P power;

    protected final List<?> modifiers;
    protected final Map<ResourceLocation, Float> cachedMaxScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> cachedPreviousMaxScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> capturedModifiedScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> capturedPreviousModifiedScales = Maps.newHashMap();
    private final Set<ResourceLocation> scalesToUpdate = Sets.newHashSet();
    protected final Set<ResourceLocation> cachedScaleIds;
    protected boolean hasLoggedWarn = false;
    protected boolean addedScales = false;

    public ApoliScaleModifier(P power, List<?> modifiers, Set<ResourceLocation> cachedScaleIds) {
        this(-128.0F, power, modifiers, cachedScaleIds);
    }

    public ApoliScaleModifier(float priority, P power, List<?> modifiers, Set<ResourceLocation> cachedScaleIds) {
        super(priority);
        this.power = power;
        this.modifiers = modifiers;
        this.cachedScaleIds = cachedScaleIds;
    }

    public Set<ResourceLocation> getCachedScaleIds() {
        return this.cachedScaleIds;
    }


    protected ResourceLocation getResourceLocationFromScaleData(ScaleData data) {
        return ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, data.getScaleType());
    }

    public CompoundTag serialize(CompoundTag tag) {
        if (!this.cachedMaxScales.isEmpty()) {
            ListTag cachedMaxScaleTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedMaxScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedMaxScaleTag.add(entryTag);
            }
            tag.put("MaxScales", cachedMaxScaleTag);
        }
        if (!this.cachedPreviousMaxScales.isEmpty()) {
            ListTag cachedPreviousMaxScaleTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedPreviousMaxScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedPreviousMaxScaleTag.add(entryTag);
            }
            tag.put("PreviousMaxScales", cachedPreviousMaxScaleTag);
        }
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if (tag.contains("MaxScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("MaxScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.cachedMaxScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        if (tag.contains("PreviousMaxScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("PreviousMaxScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.cachedPreviousMaxScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
    }

    protected void addScales(LivingEntity entity, List<ResourceLocation> scaleTypeIds) {
        if (!entity.level().isClientSide() && !this.addedScales) {
            for (ResourceLocation scaleTypeId : scaleTypeIds) {
                ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                ScaleData scaleData = scaleType.getScaleData(entity);
                ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().add(this);
                scaleData.onUpdate();
            }
            Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.addScaleToClient(entity.getId(), scaleTypeIds, this.getId()), entity);
            this.addedScales = true;
        }
    }

    public void tick(LivingEntity entity, boolean calledFromNbt) {
        boolean updatedScale = false;
        List<ResourceLocation> scaleTypeIds = ApugliPowers.MODIFY_SCALE.get().getCachedScaleIds(power, entity).stream().toList();

        addScales(entity, scaleTypeIds);

        for (ResourceLocation scaleTypeId : scaleTypeIds) {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);

            float value = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, this.capturedModifiedScales.getOrDefault(scaleTypeId, scaleData.getBaseScale()));
            if (!this.scalesToUpdate.contains(scaleTypeId)) {
                float previousValue = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, this.capturedPreviousModifiedScales.getOrDefault(scaleTypeId, scaleData.getBaseScale()));
                this.cachedMaxScales.put(scaleTypeId, value);
                this.cachedPreviousMaxScales.put(scaleTypeId, previousValue);
                this.scalesToUpdate.add(scaleTypeId);
                updatedScale = true;
            }
        }
        if (updatedScale) {
            Services.POWER.syncPower(entity, this.power);
            this.cachedScaleIds.forEach(id -> ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id).getScaleData(entity).onUpdate());
        }
    }

    @Override
    public int compareTo(ScaleModifier o)
    {
        if (!(o instanceof ApoliScaleModifier<?> ao)) {
            return -1;
        }

        final int c = Float.compare(o.getPriority(), getPriority());

        return c != 0 ? c :
                this.getId().compareTo(ao.getId());
    }

    public ResourceLocation getId() {
        return ApugliPowers.MODIFY_SCALE.get().getPowerId(this.power);
    }

    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use ApoliScaleModifier on a non-living entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }

        if (!Services.POWER.isActive(power, entity)) {
            return modifiedScale;
        }

        ResourceLocation id = getResourceLocationFromScaleData(scaleData);
        if (!capturedModifiedScales.containsKey(id) || this.capturedModifiedScales.getOrDefault(id, modifiedScale) != modifiedScale)
            this.capturedModifiedScales.put(id, modifiedScale);

        return this.cachedMaxScales.getOrDefault(id, modifiedScale);
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use ApoliScaleModifier on a non-living entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }

        if (!Services.POWER.isActive(power, entity)) {
            return modifiedScale;
        }

        ResourceLocation id = getResourceLocationFromScaleData(scaleData);
        if (!capturedPreviousModifiedScales.containsKey(id) || this.capturedPreviousModifiedScales.getOrDefault(id, modifiedScale) != modifiedScale)
            this.capturedPreviousModifiedScales.put(id, modifiedScale);

        if (this.cachedPreviousMaxScales.containsKey(id))
            return this.cachedPreviousMaxScales.get(id);

        return this.cachedPreviousMaxScales.getOrDefault(id, modifiedScale);
    }
}
