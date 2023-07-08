package net.merchantpug.apugli.platform.services;

import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.merchantpug.apugli.network.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.ValueModifyingPowerFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();
    
    /**
     * Get the modified reach distance for the entity, Fabric uses REA and Forge use its own attribute.
     * @param entity The entity to check.
     * @return The reach distance of the entity.
     */
    double getReachDistance(Entity entity);
    
    /**
     * Get the modified attack range for the entity, Fabric uses REA and Forge use its own attribute.
     * @param entity The entity to check.
     * @return The attack range of the entity.
     */
    double getAttackRange(Entity entity);


    <K> SerializableDataType<K> getKeyDataType();

    /**
     * Get the supported modifier type by platform due to the fact that new Modifier is different on Forge's end.
     * @return SerializableDataType of Modifier(on Fabric) or AttributeModifier(on Forge).
     */
    <M> SerializableDataType<M> getModifierDataType();

    /**
     * Get the supported modifier type by platform due to the fact that the new Modifier class is different on Forge's end.
     * @return SerializableDataType of List of Modifier(on Fabric) or ConfiguredModifier(on Forge).
     */
    <M> SerializableDataType<List<M>> getModifiersDataType();

    double applyModifiers(Entity entity, List<?> modifiers, double value);

    default <P> double applyModifiers(LivingEntity entity, ValueModifyingPowerFactory<P> power, double value, Predicate<P> predicate) {
        List<?> modifierList = Services.POWER.getPowers(entity, power).stream().filter(p -> predicate == null || predicate.test(p)).map(p -> power.getModifiers(p, entity)).flatMap(List::stream).toList();
        return applyModifiers(entity, modifierList, value);
    }

    default <P> double applyModifiers(LivingEntity entity, ValueModifyingPowerFactory<P> power, double value) {
        return applyModifiers(entity, power, value, null);
    }

    void sendS2C(ApugliPacketS2C packet, ServerPlayer player);

    void sendS2CTrackingAndSelf(ApugliPacketS2C packet, Entity entity);

    void sendC2S(ApugliPacketC2S packet);

    float[] getColorPowerRgba(LivingEntity entity);

    void updateKeys(SerializableData.Instance data, Player player);

    boolean isCurrentlyUsingKey(SerializableData.Instance data, Player player);

    Tuple<Integer, Integer> getHitsOnTarget(Entity actor, LivingEntity target);

    void setHitsOnTarget(Entity actor, Entity target, int initialChange, int initialTimerChange, ResourceOperation operation, ResourceOperation timerOperation);

    /*
    This exists as the default hud render for Origins Forge does not work as intended
     */
    HudRender getDefaultHudRender();

    float getEntityEyeHeight(Entity entity);

    Entity getItemStackLinkedEntity(ItemStack stack);

}
