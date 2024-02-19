package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableList;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;
import virtuoel.pehkui.api.TypedScaleModifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Stream;

public class ApoliScaleModifier<P> extends ScaleModifier {
    private static final float EPSILON = 0.0001F;

    protected final P power;
    protected LivingEntity owner;
    protected final int powerPriority;

    protected final List<?> modifiers;
    protected final Map<ResourceLocation, Float> cachedMaxScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> cachedPreviousMaxScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> checkModifiedScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> checkPreviousModifiedScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> checkScales = new HashMap<>();
    protected final Set<ResourceLocation> shouldUpdateModifiers = new HashSet<>();
    protected final Set<ResourceLocation> shouldUpdatePreviousModifiers = new HashSet<>();
    protected HashSet<ResourceLocation> notOriginalCall = new HashSet<>();
    protected HashSet<ResourceLocation> notOriginalCallPrevious = new HashSet<>();
    protected final Set<ResourceLocation> cachedScaleIds;
    private boolean addedScales = false;
    protected boolean shouldUpdate = false;
    protected boolean shouldUpdatePrevious = false;
    protected boolean initialized = false;
    protected boolean hasLoggedWarn = false;

    public ApoliScaleModifier(P power, LivingEntity entity, List<?> modifiers, Set<ResourceLocation> cachedScaleIds, int powerPriority) {
        super(Float.MIN_VALUE);
        this.power = power;
        this.owner = entity;
        this.modifiers = ImmutableList.copyOf(modifiers);
        this.cachedScaleIds = cachedScaleIds;
        this.powerPriority = powerPriority;
    }

    @Override
    public int compareTo(ScaleModifier o) {
        if (!(o instanceof ApoliScaleModifier<?> ao)) {
            return Float.compare(o.getPriority(), getPriority());
        }

        int c = Integer.compare(this.powerPriority, ao.powerPriority);

        c = c != 0 ? c : Boolean.compare(this.doModifiersContainTypedOfAnother(this.owner, ao), ao.doModifiersContainTypedOfAnother(ao.owner, this));

        if (this.modifiers.isEmpty() || ao.modifiers.isEmpty())
            c = c != 0 ? c : Boolean.compare(this.modifiers.isEmpty(), ao.modifiers.isEmpty());

        c = c != 0 ? c : Services.PLATFORM.compareModifiers(this.modifiers.stream().max(Services.PLATFORM::compareModifiers).get(), ao.modifiers.stream().max(Services.PLATFORM::compareModifiers).get());

        c = c != 0 ? c : this.getId().compareTo(ao.getId());

        return c;
    }

    public boolean doModifiersContainTypedOfAnother(Entity entity, ApoliScaleModifier<?> other) {
        return this.getCachedScaleIds().stream().map(PehkuiUtil::getScaleType).anyMatch(type -> type.getScaleData(entity).getBaseValueModifiers().stream().anyMatch(modifier -> modifier instanceof TypedScaleModifier typedScaleModifier && other.getCachedScaleIds().contains(ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, typedScaleModifier.getType()))));
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof ApoliScaleModifier<?> apoliScaleModifier)) {
            return false;
        }

        return this.getId().equals(apoliScaleModifier.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
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
        if (!this.checkModifiedScales.isEmpty()) {
            ListTag cachedPreviousMaxScaleTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkModifiedScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedPreviousMaxScaleTag.add(entryTag);
            }
            tag.put("CheckModifiedScales", cachedPreviousMaxScaleTag);
        }
        if (!this.checkPreviousModifiedScales.isEmpty()) {
            ListTag cachedPreviousMaxScaleTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkPreviousModifiedScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedPreviousMaxScaleTag.add(entryTag);
            }
            tag.put("CheckPreviousModifiedScales", cachedPreviousMaxScaleTag);
        }
        if (!this.checkScales.isEmpty()) {
            ListTag cachedPreviousMaxScaleTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedPreviousMaxScaleTag.add(entryTag);
            }
            tag.put("CheckScales", cachedPreviousMaxScaleTag);
        }
        if (!this.shouldUpdateModifiers.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.shouldUpdateModifiers) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("ShouldUpdateModifiers", listTag);
        }
        if (!this.shouldUpdatePreviousModifiers.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.shouldUpdatePreviousModifiers) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("ShouldUpdatePreviousModifiers", listTag);
        }
        if (!this.notOriginalCall.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.notOriginalCall) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("NotOriginalCall", listTag);
        }
        if (!this.notOriginalCallPrevious.isEmpty()) {
            ListTag listTag = new ListTag();
            for (ResourceLocation entry : this.notOriginalCallPrevious) {
                listTag.add(StringTag.valueOf(entry.toString()));
            }
            tag.put("NotOriginalCallPrevious", listTag);
        }
        if (this.shouldUpdate)
            tag.putBoolean("ShouldUpdate", true);

        if (this.shouldUpdatePrevious)
            tag.putBoolean("ShouldUpdatePrevious", true);

        return tag;
    }

    public void deserialize(CompoundTag tag, LivingEntity entity) {
        this.deserialize(tag, true);
        addScales(entity);
        this.updateAllScales(entity);
    }

    public void deserialize(CompoundTag tag, boolean initialize) {
        this.cachedMaxScales.clear();
        if (tag.contains("MaxScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("MaxScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.cachedMaxScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.cachedPreviousMaxScales.clear();
        if (tag.contains("PreviousMaxScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("PreviousMaxScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.cachedPreviousMaxScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkModifiedScales.clear();
        if (tag.contains("CheckModifiedScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("CheckModifiedScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.checkModifiedScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkPreviousModifiedScales.clear();
        if (tag.contains("CheckPreviousModifiedScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("CheckPreviousModifiedScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.checkPreviousModifiedScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkScales.clear();
        if (tag.contains("CheckScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("CheckScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.checkScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.shouldUpdateModifiers.clear();
        if (tag.contains("ShouldUpdateModifiers", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("ShouldUpdateModifiers", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.shouldUpdateModifiers.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.shouldUpdatePreviousModifiers.clear();
        if (tag.contains("ShouldUpdatePreviousModifiers", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("ShouldUpdatePreviousModifiers", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.shouldUpdatePreviousModifiers.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.notOriginalCall.clear();
        if (tag.contains("NotOriginalCall", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("NotOriginalCall", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.notOriginalCall.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        this.notOriginalCallPrevious.clear();
        if (tag.contains("NotOriginalCallPrevious", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("NotOriginalCallPrevious", Tag.TAG_STRING);
            for (int i = 0; i < listTag.size(); ++i) {
                this.notOriginalCallPrevious.add(new ResourceLocation(listTag.getString(i)));
            }
        }
        if (tag.contains("ShouldUpdate", Tag.TAG_BYTE))
            this.shouldUpdate = tag.getBoolean("ShouldUpdate");

        if (tag.contains("ShouldUpdatePrevious", Tag.TAG_BYTE))
            this.shouldUpdatePrevious = tag.getBoolean("ShouldUpdatePrevious");
        this.initialized = initialize;
    }

    protected void reset() {
        this.addedScales = false;
        this.hasLoggedWarn = false;
        this.shouldUpdate = false;
        this.initialized = false;
        this.cachedMaxScales.clear();
        this.cachedPreviousMaxScales.clear();
        this.checkModifiedScales.clear();
        this.checkPreviousModifiedScales.clear();
        this.checkScales.clear();
        this.notOriginalCall.clear();
        this.notOriginalCallPrevious.clear();
        this.checkScales.clear();
    }

    protected void addScales(LivingEntity entity) {
        if (!this.addedScales) {
            for (ResourceLocation scaleTypeId : this.getCachedScaleIds()) {
                ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                ScaleData scaleData = scaleType.getScaleData(entity);
                ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().remove(this);
                ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().add(this);
            }
            for (ResourceLocation scaleTypeId : this.getCachedScaleIds()) {
                ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                this.updateScale(entity, scaleType, true);
            }
            this.addedScales = true;
        }
    }

    public void tick(LivingEntity entity) {
        for (ResourceLocation typeId : getCachedScaleIds()) {
            ScaleData data = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, typeId).getScaleData(entity);

            boolean isActive = Services.POWER.isActive(this.power, entity);
            float value = !isActive ? data.getBaseScale() : (float)Services.PLATFORM.applyModifiers(data.getEntity(), this.modifiers, data.getBaseScale());
            if (this.initialized && (!compareFloats(this.checkScales.getOrDefault(typeId, data.getBaseScale()), value))) {
                this.checkScales.put(typeId, value);
                this.shouldUpdate = true;
                this.shouldUpdatePrevious = true;
                this.checkModifiedScales.clear();
                this.checkPreviousModifiedScales.clear();
            }
        }
    }

    public void updateIfShould(LivingEntity entity) {
        if (this.shouldUpdate || this.shouldUpdatePrevious) {
            updateAllScales(entity);
            this.shouldUpdate = false;
            this.shouldUpdatePrevious = false;
        }
    }

    protected void updateOthers(LivingEntity entity, boolean updateModifiers) {
        SortedSet<ApoliScaleModifier<?>> tailSet = PehkuiUtil.getModifiersInOrder(entity).tailSet(this);
        boolean skip = true;
        for (ApoliScaleModifier<?> modifier : tailSet) {
            if (skip) {
                skip = false;
                continue;
            }
            if (!modifier.notOriginalCall.isEmpty() && !modifier.notOriginalCallPrevious.isEmpty()) continue;
            modifier.notOriginalCall.addAll(modifier.getCachedScaleIds());
            modifier.notOriginalCallPrevious.addAll(modifier.getCachedScaleIds());
            modifier.shouldUpdate = true;
            modifier.shouldUpdatePrevious = true;
            modifier.scheduleForUpdate(entity, updateModifiers);
        }
    }

    public void updateAllScales(LivingEntity entity) {
        this.getCachedScaleIds().stream().map(PehkuiUtil::getScaleType).forEach(type -> updateScale(entity, type, true));
    }

    public void updateScale(LivingEntity entity, ScaleType type, boolean updateDependencies) {
        ScaleData data = type.getScaleData(entity);
        data.onUpdate();
        if (this.shouldUpdate)
            data.getScale();

        if (this.shouldUpdatePrevious)
            data.getPrevScale();

        if (updateDependencies) {
            getScaleDependencies(entity, type).forEach(scaleType -> {
                updateScale(entity, type, false);
            });
        }
    }

    private static Stream<ScaleType> getScaleDependencies(LivingEntity entity, ScaleType scaleType) {
        return ScaleRegistries.SCALE_TYPES.values().stream().filter(type -> type.getScaleData(entity).getBaseValueModifiers().stream().anyMatch(mod -> mod instanceof TypedScaleModifier typed && typed.getType() == scaleType));
    }

    public void scheduleForUpdate(LivingEntity entity, boolean updateModifiers) {
        if (updateModifiers) {
            this.shouldUpdateModifiers.addAll(this.getCachedScaleIds());
            this.shouldUpdatePreviousModifiers.addAll(this.getCachedScaleIds());
        }
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

        ResourceLocation id = getResourceLocationFromScaleData(scaleData);


        if (!Services.POWER.isActive(power, entity)) {
            this.notOriginalCall.remove(id);
            return modifiedScale;
        }

        if (!this.checkModifiedScales.containsKey(id) || !compareFloats(this.checkModifiedScales.getOrDefault(id, modifiedScale), modifiedScale) || this.shouldUpdateModifiers.contains(id)) {
            this.checkModifiedScales.put(id, modifiedScale);
            this.cachedMaxScales.put(id, (float)Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale));
            if (!this.notOriginalCall.contains(PehkuiUtil.getScaleTypeId(scaleData.getScaleType())))
                this.updateOthers(entity, true);
        }

        this.notOriginalCall.remove(id);

        return this.cachedMaxScales.getOrDefault(id, modifiedScale);
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            if (!this.hasLoggedWarn)
                Apugli.LOG.warn("Attempted to use ApoliScaleModifier on a non-living entity. This should not be possible.");
            this.hasLoggedWarn = true;
            return modifiedScale;
        }

        ResourceLocation id = getResourceLocationFromScaleData(scaleData);

        if (!Services.POWER.isActive(power, entity)) {
            this.notOriginalCallPrevious.remove(id);
            return modifiedScale;
        }

        if (!this.checkPreviousModifiedScales.containsKey(id) || !compareFloats(this.checkPreviousModifiedScales.getOrDefault(id, modifiedScale), modifiedScale) || this.shouldUpdatePreviousModifiers.contains(id)) {
            this.checkPreviousModifiedScales.put(id, modifiedScale);
            this.cachedPreviousMaxScales.put(id, (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, modifiedScale));
            if (!this.notOriginalCallPrevious.contains(PehkuiUtil.getScaleTypeId(scaleData.getScaleType())))
                this.updateOthers(entity, true);
        }
        this.notOriginalCallPrevious.remove(id);

        return this.cachedPreviousMaxScales.getOrDefault(id, modifiedScale);
    }

    protected static boolean compareFloats(float a, float b) {
        float diff = Mth.abs(a - b);
        return diff < EPSILON;
    }

    public static boolean compareDoubles(double a, double b) {
        float diff = Mth.abs((float)(a - b));
        return diff < EPSILON;
    }
}
