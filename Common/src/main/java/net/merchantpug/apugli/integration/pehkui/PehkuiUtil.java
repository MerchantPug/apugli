package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.UpdateAffectedScalesPacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A util class to separate methods that use Pehkui contents,
 * to ensure that the game runs without Pehkui on the client.
 */
public class PehkuiUtil {
    private static final List<ApoliScaleModifier<?>> MODIFIERS_IN_ORDER = Lists.newLinkedList();

    private static final Map<Entity, Set<ResourceLocation>> AFFECTED_SCALE_TYPES = Maps.newHashMap();

    public static Set<ResourceLocation> getAllAffectedScaleTypes(Entity entity) {
        return ImmutableSet.copyOf(AFFECTED_SCALE_TYPES.get(entity));
    }

    public static Set<ResourceLocation> getTypesFromCache(SerializableData.Instance data) {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        data.<ResourceLocation>ifPresent("scale_type", id -> {
            if (validate(id))
                builder.add(id);
        });
        data.<List<ResourceLocation>>ifPresent("scale_types", ids -> {
            ids.forEach(id -> {
                if (validate(id))
                    builder.add(id);
            });
        });

        return builder.build();
    }

    private static boolean validate(ResourceLocation typeId) {
        boolean value = ScaleRegistries.SCALE_TYPES.containsKey(typeId);
        if (!value) {
            Apugli.LOG.error("Identifier '{}' is not a valid scale type.", typeId);
        }
        return value;
    }

    public static ScaleType getScaleType(ResourceLocation id) {
        return ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, id);
    }

    @SuppressWarnings("unchecked")
    public static <P> void tickScalePowers(LivingEntity entity) {
        for (P power : (List<P>) Services.POWER.getPowers(entity, ApugliPowers.MODIFY_SCALE.get(), true).stream().filter(p -> ApugliPowers.MODIFY_SCALE.get().hasScaleModifier(p, entity)).sorted((p1, p2) -> {
            float firstPowerDelay = ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(p1, entity) instanceof ScaleModifier scaleModifier ? scaleModifier.getPriority() : 0.0F;
            float secondPowerDelay = ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(p2, entity) instanceof ScaleModifier scaleModifier ? scaleModifier.getPriority() : 0.0F;
            return Float.compare(firstPowerDelay, secondPowerDelay);
        }).toList()) {
            ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);

            modifier.addScales(entity, ApugliPowers.MODIFY_SCALE.get().getCachedScaleIds(power, entity).stream().toList());

            modifier.tick(entity, false);
        }
    }

    @SuppressWarnings("unchecked")
    public static <P> Object createApoliScaleModifier(P power, LivingEntity entity, SerializableData.Instance data) {
        if (!data.isPresent("modifier") && !data.isPresent("modifiers")) {
            Apugli.LOG.error("Could not create scale power modifier as the 'modifier' and 'modifiers' fields are both not specified.");
            return new ApoliScaleModifier<>(power, List.of(), Set.of(), 0);
        }
        ApoliScaleModifier<P> modifier;
        if (data.getInt("delay") > 0) {
            modifier = new LerpedApoliScaleModifier<>(power, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), data.getInt("delay"), getTypesFromCache(data), data.getInt("priority"), Optional.ofNullable(data.get("easing")));
        } else {
            modifier = new ApoliScaleModifier<>(power, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), getTypesFromCache(data), data.getInt("priority"));
        }
        AFFECTED_SCALE_TYPES.compute(entity, (entity1, ids) -> {
            if (ids == null) {
                ids = Sets.newHashSet();
            }
            ids.addAll(ApugliPowers.MODIFY_SCALE.get().getCachedScaleIds(power, entity));
            return ids;
        });

        return modifier;
    }

    public static void sendToBack(ApoliScaleModifier<?> modifier) {
        MODIFIERS_IN_ORDER.sort(Comparator.comparing(apoliScaleModifier -> apoliScaleModifier != modifier));
    }

    @SuppressWarnings("unchecked")
    public static <P> void onRemovedScalePower(P power, LivingEntity entity) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);
        ResourceLocation powerId = ApugliPowers.MODIFY_SCALE.get().getPowerId(power);

        for (ResourceLocation scaleTypeId : modifier.getCachedScaleIds()) {
            ScaleType scaleType = PehkuiUtil.getScaleType(scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);

            ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(ApugliPowers.MODIFY_SCALE.get().getPowerId(power));
            scaleData.getBaseValueModifiers().remove(modifier);
            Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.removeScaleFromClient(entity.getId(), modifier.getCachedScaleIds().stream().toList(), ApugliPowers.MODIFY_SCALE.get().getPowerId(power)), entity);
            scaleData.onUpdate();
        }

        if (Services.POWER.getPowers(entity, ApugliPowers.MODIFY_SCALE.get(), true).stream().anyMatch(p -> p instanceof LerpedApoliScaleModifier<?>)) {
            updateAffectedScales(entity);
            Services.PLATFORM.sendS2CTrackingAndSelf(new UpdateAffectedScalesPacket(entity.getId()), entity);
        }

        if (Services.POWER.getPowers(entity, ApugliPowers.MODIFY_SCALE.get(), true).size() - 1 <= 0) {
            AFFECTED_SCALE_TYPES.remove(entity);
        }
    }

    public static void updateAffectedScales(Entity entity) {
        if (!AFFECTED_SCALE_TYPES.containsKey(entity)) return;
        for (ResourceLocation scaleTypeId : AFFECTED_SCALE_TYPES.get(entity)) {
            ScaleData data = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId).getScaleData(entity);
            data.onUpdate();
            data.getScale();
            data.getPrevScale();
        }
    }

    public static <P> CompoundTag serializeScalePower(P power, LivingEntity entity, CompoundTag tag) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);
        return modifier.serialize(tag);
    }

    public static <P> void deserializeScalePower(P power, LivingEntity entity, CompoundTag tag) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);
        modifier.deserialize(tag);
        modifier.tick(entity, true);
    }

    public static float getScale(Entity entity, ResourceLocation scaleTypeId) {
        return ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId).getScaleData(entity).getScale();
    }
}
