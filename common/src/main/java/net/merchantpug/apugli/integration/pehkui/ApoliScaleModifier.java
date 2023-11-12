package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.Maps;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class ApoliScaleModifier<P> extends ScaleModifier {

    protected final P power;

    protected final List<?> modifiers;
    protected Map<ResourceLocation, Float> cachedMaxScales = Maps.newHashMap();
    protected Map<ResourceLocation, Float> cachedPreviousMaxScales = Maps.newHashMap();

    public ApoliScaleModifier(P power, List<?> modifiers) {
        this.power = power;
        this.modifiers = modifiers;
    }

    protected ResourceLocation getResourceLocationFromScaleData(ScaleData data) {
        return ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, data.getScaleType());
    }

    public CompoundTag serialize(CompoundTag tag) {
        return tag;
    }

    public void deserialize(CompoundTag tag) {

    }

    public void tick(LivingEntity entity, boolean calledFromNbt) {
        List<ResourceLocation> scaleTypeIds = ApugliPowers.MODIFY_SCALE.get().getCachedScaleIds(power, entity).stream().toList();

        for (ResourceLocation scaleTypeId : scaleTypeIds) {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);
            SortedSet<ScaleModifier> modifiers = scaleData.getBaseValueModifiers();

            if (!modifiers.contains(this) && Services.POWER.isActive(power, entity)) {
                ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().add(this);
                if (!calledFromNbt && !entity.level().isClientSide()) {
                    Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.addScaleToClient(entity.getId(), scaleTypeIds, ApugliPowers.MODIFY_SCALE.get().getPowerId(power)), entity);
                }
                scaleData.onUpdate();
            } else if (modifiers.contains(this) && !Services.POWER.isActive(power, entity)) {
                ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(this.getId());
                scaleData.getBaseValueModifiers().remove(this);
                if (!calledFromNbt && !entity.level().isClientSide()) {
                    Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.removeScaleFromClient(entity.getId(), scaleTypeIds, ApugliPowers.MODIFY_SCALE.get().getPowerId(power)), entity);
                }
                scaleData.onUpdate();
            }
        }
    }

    @Override
    public int compareTo(ScaleModifier o)
    {
        if (!(o instanceof ApoliScaleModifier ao)) {
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
        ResourceLocation id = getResourceLocationFromScaleData(scaleData);

        if (this.cachedMaxScales.containsKey(id)) {
            return this.cachedMaxScales.get(id);
        }

        float value = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale);
        this.cachedMaxScales.put(id, value);
        return value;
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        ResourceLocation id = getResourceLocationFromScaleData(scaleData);

        if (this.cachedPreviousMaxScales.containsKey(id)) {
            return this.cachedPreviousMaxScales.get(id);
        }

        float value = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale);
        this.cachedPreviousMaxScales.put(id, value);
        return value;
    }
}
