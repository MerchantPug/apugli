package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.mixin.EyeHeightAccess;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.ModelColorPower;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.apoli.util.modifier.IModifierOperation;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.merchantpug.apugli.access.ItemStackAccess;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.HitsOnTargetComponent;
import net.merchantpug.apugli.component.KeyPressComponent;
import net.merchantpug.apugli.network.ApugliPackets;
import net.merchantpug.apugli.network.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.network.s2c.AddKeyToCheckPacket;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetLessenedPacket;
import net.merchantpug.apugli.platform.services.IPlatformHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@AutoService(IPlatformHelper.class)
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
    
    @Override
    public double getReachDistance(Entity entity) {
        double base = (entity instanceof Player player && player.getAbilities().instabuild) ? 5 : 4.5;
        return (entity instanceof LivingEntity living && isModLoaded("reach-entity-attributes")) ?
            ReachEntityAttributes.getReachDistance(living, base) : base;
    }
    
    @Override
    public double getAttackRange(Entity entity) {
        double base = (entity instanceof Player player && player.getAbilities().instabuild) ? 6 : 3;
        return (entity instanceof LivingEntity living && isModLoaded("reach-entity-attributes")) ?
            ReachEntityAttributes.getAttackRange(living, base) : base;
    }

    @Override
    public SerializableDataType<Active.Key> getKeyDataType() {
        return ApoliDataTypes.KEY;
    }

    @Override
    public SerializableDataType<Modifier> getModifierDataType() {
        return Modifier.DATA_TYPE;
    }

    @Override
    public SerializableDataType<List<Modifier>> getModifiersDataType() {
        return Modifier.LIST_TYPE;
    }

    @Override
    public double applyModifiers(Entity entity, List<?> modifiers, double value) {
        return ModifierUtil.applyModifiers(entity, (List<Modifier>)modifiers, value);
    }

    @Override
    public void sendS2C(ApugliPacketS2C packet, ServerPlayer player) {
        if (player.connection != null)
            ApugliPackets.sendS2C(packet, player);
    }

    @Override
    public void sendS2CTrackingAndSelf(ApugliPacketS2C packet, Entity entity) {
        for (ServerPlayer otherPlayer : PlayerLookup.tracking(entity))
            if (otherPlayer.connection != null)
                ApugliPackets.sendS2C(packet, otherPlayer);
        if (entity instanceof ServerPlayer player && player.connection != null)
            ApugliPackets.sendS2C(packet, player);
    }

    @Override
    public void sendC2S(ApugliPacketC2S packet) {
        ApugliPackets.sendC2S(packet);
    }

    @Override
    public int compareModifiers(Object modifier, Object otherModifier) {
        return Integer.compare(compareModifiersInternal((Modifier) modifier, (Modifier) otherModifier), compareModifiersInternal((Modifier) otherModifier, (Modifier) modifier));
    }

    private int compareModifiersInternal(Modifier modifier, Modifier otherModifier) {
        IModifierOperation modifierOperation = modifier.getOperation();
        IModifierOperation modifierOperation2 = otherModifier.getOperation();
        if(modifierOperation == modifierOperation2) {
            return 0;
        } else if(modifierOperation.getPhase() == modifierOperation2.getPhase()) {
            return modifierOperation.getOrder() - modifierOperation2.getOrder();
        } else {
            return modifierOperation.getPhase().ordinal() - modifierOperation2.getPhase().ordinal();
        }
    }

    @Override
    public float[] getColorPowerRgba(LivingEntity entity) {
        List<ModelColorPower> modelColorPowers = PowerHolderComponent.getPowers(entity, ModelColorPower.class);
        if (modelColorPowers.size() > 0) {
            float red = modelColorPowers.stream().map(ModelColorPower::getRed).reduce((a, b) -> a * b).get();
            float green = modelColorPowers.stream().map(ModelColorPower::getGreen).reduce((a, b) -> a * b).get();
            float blue = modelColorPowers.stream().map(ModelColorPower::getBlue).reduce((a, c) -> a * c).get();
            float alpha = modelColorPowers.stream().map(ModelColorPower::getAlpha).min(Float::compare).get();
            return new float[] { red, green, blue, alpha };
        }
        return new float[] { 1.0F, 1.0F, 1.0F, 1.0F };
    }

    @Override
    public void updateKeys(SerializableData.Instance data, Player player) {
        if (player.level().isClientSide() && !(player.isLocalPlayer())) return;

        KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
        Active.Key key = data.get("key");
        if (!component.getKeysToCheck().contains(key)) {
            component.addKeyToCheck(key);
            if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                ApugliPackets.sendS2C(new AddKeyToCheckPacket(serverPlayer.getId(), key), serverPlayer);
            }
        }
    }

    @Override
    public boolean isCurrentlyUsingKey(SerializableData.Instance data, Player player) {
        KeyPressComponent component = ApugliEntityComponents.KEY_PRESS_COMPONENT.get(player);
        Active.Key key = data.get("key");
        return component.getCurrentlyUsedKeys().contains(key);
    }

    @Override
    public Tuple<Integer, Integer> getHitsOnTarget(Entity actor, LivingEntity target) {
        return ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.get(target).getHits().getOrDefault(actor.getId(), new Tuple<>(0, 0));
    }

    @Override
    public void setHitsOnTarget(Entity actor, Entity target, int initialChange, int initialTimerChange, ResourceOperation operation, ResourceOperation timerOperation)  {
        HitsOnTargetComponent component = ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.get(target);
        Tuple<Integer, Integer> valueTimerResetTimePair = component.getHits().getOrDefault(actor.getId(), new Tuple<>(0, 0));

        int change = operation == ResourceOperation.SET ? initialChange : valueTimerResetTimePair.getA() + initialChange;
        int timerChange = timerOperation == ResourceOperation.SET ? initialTimerChange : valueTimerResetTimePair.getB() + initialTimerChange;

        component.setHits(actor.getId(), change, timerChange);
        if (!(target instanceof ServerPlayer serverPlayer)) return;
        ApugliPackets.sendS2CTrackingAndSelf(new SyncHitsOnTargetLessenedPacket(target.getId(), component.getPreviousHits(), component.getHits()), serverPlayer);
    }

    @Override
    public HudRender getDefaultHudRender() {
        return HudRender.DONT_RENDER;
    }

    @Override
    public float getEntityEyeHeight(Entity entity) {
        return ((EyeHeightAccess)entity).callGetEyeHeight(entity.getPose(), entity.getDimensions(entity.getPose()));
    }

    @Override
    public Entity getEntityFromItemStack(ItemStack stack) {
        return stack == null ? null : ((ItemStackAccess)(Object)stack).apugli$getEntity();
    }

    @Override
    public void setEntityToItemStack(ItemStack stack, Entity entity) {
        ((ItemStackAccess)(Object)stack).apugli$setEntity(entity);
    }

    @Override
    public SerializableDataType<?> damageSourceDescriptionDataType() {
        return ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION;
    }

    @Override
    public DamageSource createDamageSource(DamageSources damageSources, SerializableData.Instance data, String typeFieldName, String descriptionFieldName) {
        return MiscUtil.createDamageSource(damageSources, data.get(descriptionFieldName), data.get(typeFieldName));
    }

    @Override
    public DamageSource createDamageSource(DamageSources damageSources, SerializableData.Instance data, Entity attacker, String typeFieldName, String descriptionFieldName) {
        return MiscUtil.createDamageSource(damageSources, data.get(descriptionFieldName), data.get(typeFieldName), attacker);
    }

    @Override
    public DamageSource createDamageSource(DamageSources damageSources, SerializableData.Instance data, Entity source, Entity attacker, String typeFieldName, String descriptionFieldName) {
        return MiscUtil.createDamageSource(damageSources, data.get(descriptionFieldName), data.get(typeFieldName), source, attacker);
    }

    @Override
    public void populateModifierIdMap(int startIndex, List<?> modifiers, Map<Object, Integer> modifierIdMap) {
        List<Modifier> modifierList = (List<Modifier>) modifiers;
        for (int i = 0; i < modifierList.size(); ++i) {
            modifierIdMap.put(modifierList.get(i), i);
            if (modifierList.get(i).getData().isPresent("modifier"))
                populateModifierIdMap(i + 1, modifierList.get(i).getData().get("modifier"), modifierIdMap);
        }
    }

}
