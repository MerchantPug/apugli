package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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

public class ApoliScaleModifier<P> extends ScaleModifier {
    private static final float EPSILON = 0.0001F;

    protected final P power;
    protected LivingEntity owner;
    protected final int powerPriority;

    protected final List<?> modifiers;
    protected final Map<ResourceLocation, Float> cachedTargetScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> cachedPreviousTargetScales = new HashMap<>();
    protected final Map<ResourceLocation, Float> checkScales = new HashMap<>();
    protected final Set<ResourceLocation> shouldUpdateModifiers = new HashSet<>();
    protected final Set<ResourceLocation> shouldUpdatePreviousModifiers = new HashSet<>();
    protected final Set<ResourceLocation> cachedScaleIds;
    private boolean addedScales = false;
    protected boolean shouldUpdate = false;
    protected boolean shouldUpdatePrevious = false;
    private boolean hasLoggedWarn = false;

    public ApoliScaleModifier(P power, LivingEntity entity, List<?> modifiers, Set<ResourceLocation> cachedScaleIds, int powerPriority) {
        super(Float.MIN_VALUE);
        this.power = power;
        this.owner = entity;
        this.modifiers = ImmutableList.copyOf(modifiers);
        this.cachedScaleIds = ImmutableSet.copyOf(cachedScaleIds);
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
        if (!this.cachedTargetScales.isEmpty()) {
            ListTag cachedTargetScalesTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedTargetScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedTargetScalesTag.add(entryTag);
            }
            tag.put("TargetScales", cachedTargetScalesTag);
        }
        if (!this.cachedPreviousTargetScales.isEmpty()) {
            ListTag cachedPreviousTargetScalesTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.cachedPreviousTargetScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                cachedPreviousTargetScalesTag.add(entryTag);
            }
            tag.put("PreviousTargetScales", cachedPreviousTargetScalesTag);
        }
        if (!this.checkScales.isEmpty()) {
            ListTag checkScalesTag = new ListTag();
            for (Map.Entry<ResourceLocation, Float> entry : this.checkScales.entrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putString("Type", entry.getKey().toString());
                entryTag.putFloat("Value", entry.getValue());
                checkScalesTag.add(entryTag);
            }
            tag.put("CheckScales", checkScalesTag);
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
        if (this.shouldUpdate)
            tag.putBoolean("ShouldUpdate", true);

        if (this.shouldUpdatePrevious)
            tag.putBoolean("ShouldUpdatePrevious", true);

        return tag;
    }

    public void deserialize(CompoundTag tag, LivingEntity entity) {
        this.addScales(entity);
        this.deserialize(tag, true);
        this.updateAllScales(entity);
    }

    public void deserialize(CompoundTag tag, boolean initialize) {
        this.cachedTargetScales.clear();
        if (tag.contains("TargetScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("TargetScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.cachedTargetScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.cachedPreviousTargetScales.clear();
        if (tag.contains("PreviousTargetScales", Tag.TAG_LIST)) {
            ListTag cachedMaxScaleTag = tag.getList("PreviousTargetScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < cachedMaxScaleTag.size(); ++i) {
                CompoundTag entryTag = cachedMaxScaleTag.getCompound(i);
                this.cachedPreviousTargetScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
            }
        }
        this.checkScales.clear();
        if (tag.contains("CheckScales", Tag.TAG_LIST)) {
            ListTag checkScalesTag = tag.getList("CheckScales", Tag.TAG_COMPOUND);
            for (int i = 0; i < checkScalesTag.size(); ++i) {
                CompoundTag entryTag = checkScalesTag.getCompound(i);
                this.cachedPreviousTargetScales.put(new ResourceLocation(entryTag.getString("Type")), entryTag.getFloat("Value"));
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
        if (tag.contains("ShouldUpdate", Tag.TAG_BYTE))
            this.shouldUpdate = tag.getBoolean("ShouldUpdate");

        if (tag.contains("ShouldUpdatePrevious", Tag.TAG_BYTE))
            this.shouldUpdatePrevious = tag.getBoolean("ShouldUpdatePrevious");
    }

    protected void reset() {
        this.addedScales = false;
        this.hasLoggedWarn = false;
        this.shouldUpdate = false;
        this.shouldUpdatePrevious = false;
        this.cachedTargetScales.clear();
        this.cachedPreviousTargetScales.clear();
        this.checkScales.clear();
        this.shouldUpdateModifiers.clear();
        this.shouldUpdatePreviousModifiers.clear();
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
                this.updateScale(entity, scaleType);
            }
            if (!entity.level().isClientSide()) {
                Services.POWER.syncPower(entity, this.power);
            }
            this.addedScales = true;
        }
    }

    public void tick(LivingEntity entity) {
        boolean sync = false;

        for (ResourceLocation typeId : getCachedScaleIds()) {
            ScaleData data = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, typeId).getScaleData(entity);

            boolean isActive = Services.POWER.isActive(this.power, entity);
            float value = !isActive ? data.getBaseScale() : (float)Services.PLATFORM.applyModifiers(data.getEntity(), this.modifiers, data.getBaseScale());
            if ((!compareFloats(this.checkScales.getOrDefault(typeId, data.getBaseScale()), value))) {
                this.checkScales.put(typeId, value);
                this.markForUpdating(typeId, false);
                sync = true;
            }
        }

        if (sync) {
            Services.POWER.syncPower(entity, this.power);
            this.updateOthers(entity);
        }
    }

    protected void markForUpdating(ResourceLocation typeId, boolean notOriginalCall) {
        this.shouldUpdateModifiers.add(typeId);
        this.shouldUpdatePreviousModifiers.add(typeId);
        this.shouldUpdate = true;
        this.shouldUpdatePrevious = true;
        if (notOriginalCall) {
            this.cachedTargetScales.clear();
            this.cachedPreviousTargetScales.clear();
        }
    }

    protected void updateIfShould(LivingEntity entity) {
        if (this.shouldUpdate || this.shouldUpdatePrevious) {
            updateAllScales(entity);
            this.shouldUpdate = false;
            this.shouldUpdatePrevious = false;
        }
    }

    public void updateOthers(LivingEntity entity) {
        SortedSet<ApoliScaleModifier<?>> tailSet = PehkuiUtil.getModifiersInOrder(entity).tailSet(this);
        for (ApoliScaleModifier<?> modifier : tailSet) {
            if (modifier.getId().equals(this.getId()))
                continue;

            modifier.getCachedScaleIds().forEach(id -> modifier.markForUpdating(id, true));
            Services.POWER.syncPower(entity, modifier.power);
        }
    }

    public void updateAllScales(LivingEntity entity) {
        this.getCachedScaleIds().stream().map(PehkuiUtil::getScaleType).forEach(type -> updateScale(entity, type));
    }

    public void updateScale(LivingEntity entity, ScaleType type) {
        ScaleData data = type.getScaleData(entity);
        data.onUpdate();
        if (this.shouldUpdate)
            data.getScale();

        if (this.shouldUpdatePrevious)
            data.getPrevScale();
    }

    public ResourceLocation getId() {
        return ApugliPowers.MODIFY_SCALE.get().getPowerId(this.power);
    }

    public ResourceLocation getPowerId() {
        return ApugliPowers.MODIFY_SCALE.get().getPowerId(this.power);
    }


    @Override
    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            logWarn();
            return modifiedScale;
        }

        ResourceLocation id = getResourceLocationFromScaleData(scaleData);


        if (!Services.POWER.isActive(power, entity)) {
            return modifiedScale;
        }

        if (this.shouldUpdateModifiers.contains(id)) {
            this.cachedTargetScales.put(id, (float)Services.PLATFORM.applyModifiers(scaleData.getEntity(), this.modifiers, modifiedScale));
        }

        return this.cachedTargetScales.getOrDefault(id, modifiedScale);
    }

    @Override
    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        if (!(scaleData.getEntity() instanceof LivingEntity entity)) {
            logWarn();
            return modifiedScale;
        }

        ResourceLocation id = getResourceLocationFromScaleData(scaleData);

        if (!Services.POWER.isActive(power, entity)) {
            return modifiedScale;
        }

        if (this.shouldUpdatePreviousModifiers.contains(id)) {
            this.cachedPreviousTargetScales.put(id, (float) Services.PLATFORM.applyModifiers(entity, this.modifiers, modifiedScale));
        }

        return this.cachedPreviousTargetScales.getOrDefault(id, modifiedScale);
    }

    protected static boolean compareFloats(float a, float b) {
        float diff = Mth.abs(a - b);
        return diff < EPSILON;
    }

    protected void logWarn() {
        if (!this.hasLoggedWarn)
            Apugli.LOG.warn("Attempted to use ApoliScaleModifier on a non-living entity. This should not be possible.");
        this.hasLoggedWarn = true;
    }
}
