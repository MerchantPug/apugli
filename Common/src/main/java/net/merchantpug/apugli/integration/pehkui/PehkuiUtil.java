package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableSet;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A util class to separate methods that use Pehkui contents,
 * to ensure that the game runs without Pehkui on the client.
 */
public class PehkuiUtil {

    public static <P> Set<ResourceLocation> getTypesFromCache(P power) {
        SerializableData.Instance data = ApugliPowers.MODIFY_SCALE.get().getDataFromPower(power);
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        data.<ResourceLocation>ifPresent("scale_type", builder::add);
        data.<List<ResourceLocation>>ifPresent("scale_types", builder::addAll);

        return builder.build();
    }

    public static ScaleType getScaleType(ResourceLocation id) {
        return ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id);
    }

    @SuppressWarnings("unchecked")
    public static <P> void tickScalePower(P power, LivingEntity entity) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);

        if (modifier != null)
            modifier.tick(entity, false);
    }

    @SuppressWarnings("unchecked")
    public static <P> Object createApoliScaleModifier(P power, Entity entity) {
        SerializableData.Instance data = ApugliPowers.MODIFY_SCALE.get().getDataFromPower(power);

        if (!data.isPresent("modifier") && !data.isPresent("modifiers")) {
            Apugli.LOG.error("Could not create scale power modifier as the 'modifier' and 'modifiers' fields are both not specified.");
            return new ApoliScaleModifier<>(power, List.of());
        }
        ApoliScaleModifier<P> modifier;
        if (data.isPresent("delay")) {
            modifier = new LerpedApoliScaleModifier<>(power, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), data.getInt("delay"), Optional.empty());
        } else {
            modifier = new ApoliScaleModifier<>(power, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity));
        }

        return modifier;
    }

    @SuppressWarnings("unchecked")
    public static <P> void onRemovedScalePower(P power, LivingEntity entity) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);

        for (ResourceLocation scaleTypeId : (Set<ResourceLocation>)ApugliPowers.MODIFY_SCALE.get().getCachedScaleIds(power, entity)) {
            ScaleType scaleType = PehkuiUtil.getScaleType(scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);

            ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(ApugliPowers.MODIFY_SCALE.get().getPowerId(power));
            scaleData.getBaseValueModifiers().remove(modifier);
            Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.removeScaleFromClient(entity.getId(), ApugliPowers.MODIFY_SCALE.get().getCachedScaleIds(power, entity).stream().toList(), ApugliPowers.MODIFY_SCALE.get().getPowerId(power)), entity);
        }
    }

    public static <P> CompoundTag serializeScalePower(P power, LivingEntity entity, CompoundTag tag) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);

        if (modifier instanceof LerpedApoliScaleModifier<P> lerpedModifier) {
            tag.putInt("Delay", lerpedModifier.getTicks());
            if (lerpedModifier.getPreviousScale().isPresent()) {
                tag.putFloat("PreviousScale", lerpedModifier.getPreviousScale().get());
            }
        }

        return tag;
    }

    public static <P> void deserializeScalePower(P power, LivingEntity entity, CompoundTag tag) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);
        if (modifier instanceof LerpedApoliScaleModifier<?> lerpedModifier) {
            int delay = tag.getInt("Delay");
            lerpedModifier.setTicks(delay);
            if (tag.contains("PreviousScale", Tag.TAG_FLOAT)) {
                float previousScale = tag.getFloat("PreviousScale");
                lerpedModifier.setPreviousScale(previousScale);
            }
        }
        if (modifier != null)
            modifier.tick(entity, true);
    }

    public static float getScale(Entity entity, ResourceLocation scaleTypeId) {
        return ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId).getScaleData(entity).getScale();
    }
}
