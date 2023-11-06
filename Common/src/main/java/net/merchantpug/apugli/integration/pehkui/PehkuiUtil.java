package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableSet;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.ScalePowerData;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A helper class to separate methods that use Pehkui contents,
 * to ensure that the game runs without Pehkui on the client.
 */
public class PehkuiUtil {
    private static final Map<Entity, Map<Object, Set<ResourceLocation>>> TYPE_CACHE = new HashMap<>();
    private static final Map<Entity, Map<ResourceLocation, ApoliScaleModifier<?>>> MODIFIER_CACHE = new HashMap<>();

    public static <P> Set<ResourceLocation> getTypesFromCache(P power, Entity entity) {
        if (!TYPE_CACHE.containsKey(entity) || !TYPE_CACHE.get(entity).containsKey(power)) {
            SerializableData.Instance data = ApugliPowers.MODIFY_SCALE.get().getDataFromPower(power);
            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

            data.<ResourceLocation>ifPresent("scale_type", builder::add);
            data.<List<ResourceLocation>>ifPresent("scale_types", builder::addAll);

            if (!TYPE_CACHE.containsKey(entity)) {
                TYPE_CACHE.put(entity, new HashMap<>());
            }
            TYPE_CACHE.get(entity).put(power, builder.build());
        }
        return TYPE_CACHE.get(entity).get(power);
    }

    public static <P> void removeTypesFromCache(P power, Entity entity) {
        if (TYPE_CACHE.containsKey(entity)) {
            TYPE_CACHE.get(entity).remove(power);
            if (TYPE_CACHE.get(entity).isEmpty())
                TYPE_CACHE.remove(entity);
        }
    }
    
    public static @Nullable <P> ApoliScaleModifier<P> getModifierFromCache(ResourceLocation id, Entity entity) {
        if (!MODIFIER_CACHE.containsKey(entity) || !MODIFIER_CACHE.get(entity).containsKey(id)) {
            return null;
        }
        return (ApoliScaleModifier<P>) MODIFIER_CACHE.get(entity).get(id);
    }

    public static <P> void addModifierToCache(ResourceLocation id, Entity entity, ApoliScaleModifier<P> modifier) {
        if (!MODIFIER_CACHE.containsKey(entity)) {
            MODIFIER_CACHE.put(entity, new HashMap<>());
        }
        MODIFIER_CACHE.get(entity).put(id, modifier);
    }
    public static void removeModifierFromCache(ResourceLocation id, Entity entity) {
        if (MODIFIER_CACHE.containsKey(entity)) {
            MODIFIER_CACHE.get(entity).remove(id);
            if (MODIFIER_CACHE.get(entity).isEmpty())
                MODIFIER_CACHE.remove(entity);
        }
    }

    public static ScaleType getScaleType(ResourceLocation id) {
        return ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id);
    }

    @SuppressWarnings("unchecked")
    public static <P> void tickScalePower(P power, LivingEntity entity) {
        // Failsafe to make sure that this won't run without Pehkui.
        if (!Services.PLATFORM.isModLoaded("pehkui")) return;

        ResourceLocation mappedScaleModifierId = ApugliPowers.MODIFY_SCALE.get().getMappedScaleModifierId(power);
        ApoliScaleModifier<P> modifier = PehkuiUtil.getModifierFromCache(mappedScaleModifierId, entity);

        if (modifier != null)
            modifier.tick(entity);
    }

    @SuppressWarnings("unchecked")
    public static <P> void onAddedScalePower(P power, LivingEntity entity) {
        // Failsafe to make sure that this won't run without Pehkui.
        if (!Services.PLATFORM.isModLoaded("pehkui") || entity.isDeadOrDying()) return;

        ResourceLocation mappedScaleModifierId = ApugliPowers.MODIFY_SCALE.get().getMappedScaleModifierId(power);
        SerializableData.Instance data = ApugliPowers.MODIFY_SCALE.get().getDataFromPower(power);
        MODIFIER_CACHE.keySet().removeIf(entity1 -> entity1.getId() == entity.getId());
        if (data.isPresent("delay")) {
            PehkuiUtil.addModifierToCache(mappedScaleModifierId, entity, new LerpedApoliScaleModifier<>(power, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), mappedScaleModifierId, data.getInt("delay"), Optional.of(1.0F)));
        } else {
            PehkuiUtil.addModifierToCache(mappedScaleModifierId, entity, new ApoliScaleModifier<>(power, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), mappedScaleModifierId));
        }
    }

    @SuppressWarnings("unchecked")
    public static <P> void onRemovedScalePower(P power, LivingEntity entity) {
        // Failsafe to make sure that this won't run without Pehkui.
        if (!Services.PLATFORM.isModLoaded("pehkui")) return;

        ResourceLocation mappedScaleModifierId = ApugliPowers.MODIFY_SCALE.get().getMappedScaleModifierId(power);

        if (!entity.isDeadOrDying()) {
            ApoliScaleModifier<P> modifier = PehkuiUtil.getModifierFromCache(mappedScaleModifierId, entity);

            for (ResourceLocation scaleTypeId : getTypesFromCache(power, entity)) {
                ScaleType scaleType = PehkuiUtil.getScaleType(scaleTypeId);
                ScaleData scaleData = scaleType.getScaleData(entity);

                ((ScaleDataAccess)scaleData).apugli$removeFromApoliScaleModifiers(mappedScaleModifierId);
                scaleData.getBaseValueModifiers().remove(modifier);
            }
        }
        Services.PLATFORM.sendS2CTrackingAndSelf(new SyncScalePacket(entity.getId(), getTypesFromCache(power, entity).stream().toList(), ApugliPowers.MODIFY_SCALE.get().getPowerId(power), true), entity);
        PehkuiUtil.removeModifierFromCache(mappedScaleModifierId, entity);
        removeTypesFromCache(power, entity);
    }

    public static <P> void scalePowerToTag(P power, LivingEntity entity, CompoundTag tag) {
        ResourceLocation mappedScaleModifierId = ApugliPowers.MODIFY_SCALE.get().getMappedScaleModifierId(power);

        if (PehkuiUtil.getModifierFromCache(mappedScaleModifierId, entity) instanceof LerpedApoliScaleModifier<?> lasm) {
            tag.putInt("Delay", lasm.getTicks());
            if (lasm.getPreviousScale().isPresent()) {
                tag.putFloat("PreviousScale", lasm.getPreviousScale().get());
            }
        }
    }

    public static <P> void scalePowerFromTag(P power, LivingEntity entity, CompoundTag tag) {
        // Failsafe to make sure that this won't run without Pehkui.
        if (!Services.PLATFORM.isModLoaded("pehkui")) return;

        ResourceLocation mappedScaleModifierId = ApugliPowers.MODIFY_SCALE.get().getMappedScaleModifierId(power);

        if (PehkuiUtil.getModifierFromCache(mappedScaleModifierId, entity) == null) {
            SerializableData.Instance data = ApugliPowers.MODIFY_SCALE.get().getDataFromPower(power);
            if (data.isPresent("delay")) {
                LerpedApoliScaleModifier<?> modifier = new LerpedApoliScaleModifier<>(power, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), mappedScaleModifierId, data.getInt("delay"), tag.contains("PreviousScale", Tag.TAG_FLOAT) ? Optional.of(tag.getFloat("PreviousScale")) : Optional.empty());
                modifier.setTicks(tag.getInt("Delay"));
                PehkuiUtil.addModifierToCache(mappedScaleModifierId, entity, modifier);
            } else {
                PehkuiUtil.addModifierToCache(mappedScaleModifierId, entity, new ApoliScaleModifier<>(power, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), mappedScaleModifierId));
            }
            ApoliScaleModifier<P> modifier = PehkuiUtil.getModifierFromCache(mappedScaleModifierId, entity);

            if (modifier != null)
                modifier.tick(entity);
        } else if (PehkuiUtil.getModifierFromCache(mappedScaleModifierId, entity) instanceof LerpedApoliScaleModifier<?> modifier) {
            modifier.setTicks(tag.getInt("Delay") - 1);
            if (tag.contains("PreviousScale", Tag.TAG_FLOAT)) {
                modifier.setPreviousScale(tag.getFloat("PreviousScale"));
            }

            modifier.tick(entity);
        }
    }

    public static float getScale(Entity entity, ResourceLocation scaleTypeId) {
        return ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId).getScaleData(entity).getScale();
    }

    public static List<ScalePowerData> getScalePowerData(LivingEntity entity) {
        List<ScalePowerData> scalePowerDataList = new ArrayList<>();
        for (Object power : Services.POWER.getPowers(entity, ApugliPowers.MODIFY_SCALE.get(), true)) {
            ResourceLocation mappedScaleModifierId = ApugliPowers.MODIFY_SCALE.get().getMappedScaleModifierId(power);
            ApoliScaleModifier<?> modifier = PehkuiUtil.getModifierFromCache(mappedScaleModifierId, entity);

            Optional<Integer> lerpTicks = modifier instanceof LerpedApoliScaleModifier<?> lerped ? Optional.of(lerped.getTicks()) : Optional.empty();
            Optional<Integer> lerpMax = modifier instanceof LerpedApoliScaleModifier<?> lerped ? Optional.of(lerped.getMaxTicks()) : Optional.empty();
            Optional<Float> previousScale = modifier instanceof LerpedApoliScaleModifier<?> lerped ? lerped.getPreviousScale() : Optional.empty();

            scalePowerDataList.add(new ScalePowerData(ApugliPowers.MODIFY_SCALE.get().getPowerId(power), ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), lerpTicks, lerpMax, previousScale));
        }
        return scalePowerDataList;
    }
}
