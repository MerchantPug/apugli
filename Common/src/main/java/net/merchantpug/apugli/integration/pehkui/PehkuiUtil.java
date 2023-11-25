package net.merchantpug.apugli.integration.pehkui;

import com.google.common.collect.ImmutableSet;
import io.github.apace100.calio.data.SerializableData;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.network.s2c.integration.pehkui.RemoveModifierInOrderPacket;
import net.merchantpug.apugli.network.s2c.integration.pehkui.SyncScalePacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

/**
 * A util class to separate methods that use Pehkui contents,
 * to ensure that the game runs without Pehkui.
 */
public class PehkuiUtil {
    private static final Map<Entity, SortedSet<ApoliScaleModifier<?>>> MODIFIERS_IN_ORDER = new HashMap<>();

    private static void createModifiersInOrder(LivingEntity entity, ApoliScaleModifier<?> modifier, boolean remove) {
        if (!MODIFIERS_IN_ORDER.containsKey(entity)) {
            MODIFIERS_IN_ORDER.put(entity, new ObjectAVLTreeSet<>());
        }

        if (!remove) {
            MODIFIERS_IN_ORDER.get(entity).add(modifier);
        } else {
            MODIFIERS_IN_ORDER.get(entity).remove(modifier);
            if (MODIFIERS_IN_ORDER.get(entity).isEmpty())
                MODIFIERS_IN_ORDER.remove(entity);
        }

        if (!entity.level().isClientSide() && remove)
            Services.PLATFORM.sendS2CTrackingAndSelf(RemoveModifierInOrderPacket.removePacket(entity.getId(), modifier.getId()), entity);
    }

    public static SortedSet<ApoliScaleModifier<?>> getModifiersInOrder(LivingEntity entity) {
        if (!MODIFIERS_IN_ORDER.containsKey(entity)) {
            return new ObjectAVLTreeSet<>();
        }
        return MODIFIERS_IN_ORDER.get(entity);
    }

    public static Set<ResourceLocation> getTypesFromCache(SerializableData.Instance data) {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

        data.<ResourceLocation>ifPresent("scale_type", id -> {
            if (validate(id))
                builder.add(id);
        });
        data.<List<ResourceLocation>>ifPresent("scale_types", ids -> ids.forEach(id -> {
            if (validate(id))
                builder.add(id);
        }));

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

    public static ResourceLocation getScaleTypeId(ScaleType type) {
        return ScaleRegistries.getId(ScaleRegistries.SCALE_TYPES, type);
    }

    @SuppressWarnings("unchecked")
    public static void tickScalePowers(LivingEntity entity) {
        if (!MODIFIERS_IN_ORDER.containsKey(entity) || MODIFIERS_IN_ORDER.get(entity).isEmpty() || MODIFIERS_IN_ORDER.get(entity).size() != Services.POWER.getPowers(entity, ApugliPowers.MODIFY_SCALE.get(), true).size()) return;

        for (ApoliScaleModifier<?> modifier : MODIFIERS_IN_ORDER.get(entity)) {
            if (!entity.level().isClientSide()) {
                modifier.addScales(entity);
                modifier.tick(entity);
            }
            modifier.updateIfShould(entity);
        }
    }

    @SuppressWarnings("unchecked")
    public static <P> void onAddedOrRespawnedScalePower(P power, LivingEntity entity) {
        if (!(ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity) instanceof ApoliScaleModifier<?> scaleModifier)) return;
        scaleModifier.initialized = true;
    }

    @SuppressWarnings("unchecked")
    public static <P> Object createApoliScaleModifier(P power, LivingEntity entity, SerializableData.Instance data) {
        if (!data.isPresent("modifier") && !data.isPresent("modifiers")) {
            Apugli.LOG.error("Could not create scale power modifier as the 'modifier' and 'modifiers' fields are both not specified.");
            return new ApoliScaleModifier<>(power, entity, List.of(), Set.of(), 0);
        }
        ApoliScaleModifier<P> modifier;
        if (data.getInt("delay") > 0) {
            modifier = new DelayedApoliScaleModifier<>(power, entity, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), ApugliPowers .MODIFY_SCALE.get().getDelayModifiers(power, entity), data.getInt("delay"), getTypesFromCache(data), data.getInt("priority"), Optional.ofNullable(data.get("easing")));
        } else {
            modifier = new ApoliScaleModifier<>(power, entity, ApugliPowers.MODIFY_SCALE.get().getModifiers(power, entity), getTypesFromCache(data), data.getInt("priority"));
        }
        if (entity != null)
            createModifiersInOrder(entity, modifier, false);

        return modifier;
    }

    @SuppressWarnings("unchecked")
    public static <P> void onRemovedScalePower(P power, LivingEntity entity) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);

        removeModifierFromOrderList(entity, modifier);
        for (ResourceLocation scaleTypeId : modifier.getCachedScaleIds()) {
            ScaleType scaleType = PehkuiUtil.getScaleType(scaleTypeId);
            ScaleData scaleData = scaleType.getScaleData(entity);

            ((ScaleDataAccess) scaleData).apugli$removeFromApoliScaleModifiers(ApugliPowers.MODIFY_SCALE.get().getPowerId(power));
            scaleData.getBaseValueModifiers().remove(modifier);
            scaleData.onUpdate();
        }
        Services.PLATFORM.sendS2CTrackingAndSelf(SyncScalePacket.removeScaleFromClient(entity.getId(), modifier.getCachedScaleIds().stream().toList(), ApugliPowers.MODIFY_SCALE.get().getPowerId(power)), entity);
    }

    public static void removeModifierFromOrderList(LivingEntity entity, ApoliScaleModifier<?> modifier) {
        createModifiersInOrder(entity, modifier, true);
    }

    public static <P> CompoundTag serializeScalePower(P power, LivingEntity entity, CompoundTag tag) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);
        return modifier.serialize(tag);
    }

    public static <P> void deserializeScalePower(P power, LivingEntity entity, CompoundTag tag) {
        ApoliScaleModifier<P> modifier = (ApoliScaleModifier<P>) ApugliPowers.MODIFY_SCALE.get().getApoliScaleModifier(power, entity);
        modifier.deserialize(tag, entity);
    }

    public static float getScale(Entity entity, ResourceLocation scaleTypeId) {
        return ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, scaleTypeId).getScaleData(entity).getScale();
    }
}
