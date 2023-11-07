package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.Maps;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
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
    protected Map<ScaleType, Float> cachedMaxScales = Maps.newHashMap();
    protected Map<ScaleType, Float> cachedPrevMaxScales = Maps.newHashMap();

    public ApoliScaleModifier(P power, List<?> modifiers) {
        this.power = power;
        this.modifiers = modifiers;
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
        float maxScale = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale);

        if (!this.cachedMaxScales.containsKey(scaleData.getScaleType()) || maxScale != this.cachedMaxScales.get(scaleData.getScaleType())) {
            this.cachedMaxScales.put(scaleData.getScaleType(), maxScale);
        }

        return this.cachedMaxScales.get(scaleData.getScaleType());
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        float maxScale = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale);

        if (!this.cachedPrevMaxScales.containsKey(scaleData.getScaleType()) || maxScale != this.cachedPrevMaxScales.get(scaleData.getScaleType())) {
            this.cachedPrevMaxScales.put(scaleData.getScaleType(), maxScale);
        }

        return this.cachedPrevMaxScales.get(scaleData.getScaleType());
    }
}
