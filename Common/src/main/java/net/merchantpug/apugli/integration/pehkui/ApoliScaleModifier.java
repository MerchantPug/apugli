package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.Maps;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApoliScaleModifier<P> extends ScaleModifier {

    protected final P power;
    private final int powerPriority;
    private int numericalId;

    protected final List<?> modifiers;
    protected final Map<ResourceLocation, Float> cachedMaxScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> cachedPreviousMaxScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> checkModifiedScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> checkPreviousModifiedScales = Maps.newHashMap();
    protected final Map<ResourceLocation, Float> checkMaxScale = Maps.newHashMap();
    protected final Set<ResourceLocation> cachedScaleIds;
    protected boolean hasLoggedWarn = false;
    private boolean addedScales = false;

    public ApoliScaleModifier(P power, List<?> modifiers, Set<ResourceLocation> cachedScaleIds, int powerPriority) {
        super(0.0F);
        this.power = power;
        this.modifiers = modifiers;
        this.cachedScaleIds = cachedScaleIds;
        this.powerPriority = powerPriority;
    }

    @Override
    public int compareTo(ScaleModifier o) {
        if (!(o instanceof ApoliScaleModifier<?> ao)) {
            return -1;
        }

        int c = this.getId().compareTo(ao.getId());
        c = c != 0 ? c :
                Integer.compare(this.powerPriority, ao.powerPriority);

        return c;
    }

    public Set<ResourceLocation> getCachedScaleIds() {
        return this.cachedScaleIds;
    }

    protected ResourceLocation getResourceLocationFromScaleData(ScaleData data) {
        return ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, data.getScaleType());
    }

    public CompoundTag serialize(CompoundTag tag) {
        tag.putInt("NumericalId", this.numericalId);
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
        this.numericalId = tag.getInt("NumericalId");
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
            }
            Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.addScaleToClient(entity.getId(), scaleTypeIds, this.getId()), entity);
            for (ResourceLocation scaleTypeId : scaleTypeIds) {
                ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                ScaleData scaleData = scaleType.getScaleData(entity);
                scaleData.onUpdate();
                scaleData.getScale();
                scaleData.getPrevScale();
            }
            this.addedScales = true;
        }
    }

    public void tick(LivingEntity entity, boolean calledFromNbt) {
        boolean updatedScale = false;

        for (ResourceLocation typeId : getCachedScaleIds()) {
            ScaleData data = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, typeId).getScaleData(entity);

            float value = (float)Services.PLATFORM.applyModifiers(data.getEntity(), this.modifiers, data.getBaseScale());
            if (!this.checkMaxScale.containsKey(typeId) || value != this.checkMaxScale.get(typeId)) {
                if (value != this.checkMaxScale.getOrDefault(typeId, value)) {
                    PehkuiUtil.sendToBack(this);
                }
                this.checkMaxScale.put(typeId, value);
                updatedScale = true;
            }
        }

        if (updatedScale) {
            this.updateAllScales(entity);
        }
    }

    public void updateAllScales(LivingEntity entity) {
        PehkuiUtil.getAllAffectedScaleTypes(entity).forEach(id -> {
            ScaleData data = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id).getScaleData(entity);
            data.onUpdate();
            data.getScale();
            data.getPrevScale();
        });
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
        if (!checkModifiedScales.containsKey(id) || this.checkModifiedScales.get(id) != modifiedScale) {
            this.checkModifiedScales.put(id, modifiedScale);
            this.cachedMaxScales.put(id, (float)Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale));
        }

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
        if (!checkModifiedScales.containsKey(id) || this.checkModifiedScales.get(id) != modifiedScale) {
            this.checkModifiedScales.put(id, modifiedScale);
            this.cachedPreviousMaxScales.put(id, (float)Services.PLATFORM.applyModifiers(entity, this.modifiers, modifiedScale));
        }

        if (this.cachedPreviousMaxScales.containsKey(id))
            return this.cachedPreviousMaxScales.get(id);

        return this.cachedPreviousMaxScales.getOrDefault(id, modifiedScale);
    }
}
