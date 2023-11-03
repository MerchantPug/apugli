package net.merchantpug.apugli.integration.pehkui;

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
import java.util.Optional;
import java.util.SortedSet;

public class ApoliScaleModifier<P> extends ScaleModifier {

    protected final P power;

    protected final List<?> modifiers;
    private final ResourceLocation id;
    protected float cachedMaxScale = 1.0F;
    protected float cachedPrevMaxScale = 1.0F;

    public ApoliScaleModifier(P power, List<?> modifiers, ResourceLocation id) {
        super();
        this.power = power;
        this.modifiers = modifiers;
        this.id = id;
    }

    public void tick(LivingEntity living) {
        if (!living.level().isClientSide()) {
            boolean hasSentPacket = false;
            List<ResourceLocation> scaleTypeIds = ApugliPowers.MODIFY_SCALE.get().getScaleTypeCache(power, living).stream().toList();

            for (ResourceLocation scaleTypeId : scaleTypeIds) {
                ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId);
                ScaleData scaleData = scaleType.getScaleData(living);
                SortedSet<ScaleModifier> modifiers = scaleData.getBaseValueModifiers();

                if (!modifiers.contains(this) && Services.POWER.isActive(power, living)) {
                    ((ScaleDataAccess) scaleData).apugli$addToApoliScaleModifiers(this.getId());
                    scaleData.getBaseValueModifiers().add(this);
                    if (!hasSentPacket) {
                        Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(living.getId(), scaleTypeIds, ApugliPowers.MODIFY_SCALE.get().getPowerId(power), ApugliPowers.MODIFY_SCALE.get().getModifiers(power, living), Optional.empty(), Optional.empty()), living);
                    }
                    scaleData.onUpdate();
                } else if (modifiers.contains(this) && !Services.POWER.isActive(power, living)) {
                    ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(this.getId());
                    scaleData.getBaseValueModifiers().remove(this);
                    if (!hasSentPacket) {
                        Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(living.getId(), scaleTypeIds, ApugliPowers.MODIFY_SCALE.get().getPowerId(power), false), living);
                    }
                    scaleData.onUpdate();
                }
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
                this.id.compareTo(ao.id);
    }

    public ResourceLocation getId() {
        return id;
    }

    public float modifyScale(final ScaleData scaleData, final float modifiedScale, final float delta) {
        float maxScale = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale);

        if (maxScale != this.cachedMaxScale) {
            this.cachedMaxScale = maxScale;
        }

        return this.cachedMaxScale;
    }

    public float modifyPrevScale(final ScaleData scaleData, final float modifiedScale) {
        float maxScale = (float) Services.PLATFORM.applyModifiers(scaleData.getEntity(), modifiers, modifiedScale);

        if (maxScale != this.cachedPrevMaxScale) {
            this.cachedPrevMaxScale = maxScale;
        }

        return this.cachedPrevMaxScale;
    }
}
