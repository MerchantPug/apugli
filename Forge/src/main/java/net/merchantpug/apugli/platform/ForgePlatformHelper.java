package net.merchantpug.apugli.platform;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.MiscUtil;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.apoli.util.modifier.ModifierUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.IActivePower;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.ModelColorPower;
import io.github.edwinmindcraft.apoli.common.power.configuration.ColorConfiguration;
import io.github.edwinmindcraft.apoli.common.registry.ApoliPowers;
import net.merchantpug.apugli.capability.entity.HitsOnTargetCapability;
import net.merchantpug.apugli.capability.entity.KeyPressCapability;
import net.merchantpug.apugli.data.ApoliForgeDataTypes;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.c2s.ApugliPacketC2S;
import net.merchantpug.apugli.network.s2c.AddKeyToCheckPacket;
import net.merchantpug.apugli.network.s2c.ApugliPacketS2C;
import net.merchantpug.apugli.network.s2c.SyncHitsOnTargetLessenedPacket;
import net.merchantpug.apugli.network.s2c.ForcePlayerPosePacket;
import net.merchantpug.apugli.platform.services.IPlatformHelper;
import net.merchantpug.apugli.util.ActiveKeyUtil;
import net.merchantpug.apugli.util.HudRenderUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
@AutoService(IPlatformHelper.class)
public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
    
    @Override
    public double getReachDistance(Entity entity) {
        return entity instanceof IForgePlayer player ? player.getBlockReach() : 4.5;
    }
    
    @Override
    public double getAttackRange(Entity entity) {
        return entity instanceof IForgePlayer player ? player.getEntityReach() : 3;
    }

    @Override
    public SerializableDataType<IActivePower.Key> getKeyDataType() {
        return ApoliForgeDataTypes.KEY;
    }

    @Override
    public SerializableDataType<ConfiguredModifier<?>> getModifierDataType() {
        return ApoliForgeDataTypes.MODIFIER;
    }

    @Override
    public SerializableDataType<List<ConfiguredModifier<?>>> getModifiersDataType() {
        return SerializableDataType.list(ApoliForgeDataTypes.MODIFIER);
    }

    @Override
    public double applyModifiers(Entity entity, List<?> modifiers, double value) {
        if (modifiers.stream().anyMatch(o -> o instanceof ConfiguredModifier<?>))
            return ModifierUtil.applyModifiers(entity, (List<ConfiguredModifier<?>>) modifiers, value);
        return value;
    }

    @Override
    public void sendS2C(ApugliPacketS2C packet, ServerPlayer player) {
        ApugliPacketHandler.sendS2C(packet, player);
    }

    @Override
    public void sendS2CTrackingAndSelf(ApugliPacketS2C packet, Entity entity) {
        ApugliPacketHandler.sendS2CTrackingAndSelf(packet, entity);
    }

    @Override
    public void sendC2S(ApugliPacketC2S packet) {
        ApugliPacketHandler.sendC2S(packet);
    }

    @Override
    public int compareModifiers(Object modifier, Object otherModifier) {
        return 0;
    }

    private int compareModifiersInternal(ConfiguredModifier<?> modifier, ConfiguredModifier<?> otherModifier) {
        if (modifier.getFactory() == otherModifier.getFactory())
            return 0;
        else if (modifier.getFactory().getPhase() == otherModifier.getFactory().getPhase())
            return modifier.getFactory().getOrder() - otherModifier.getFactory().getOrder();
        else
            return modifier.getFactory().getPhase().ordinal() - otherModifier.getFactory().getPhase().ordinal();
    }

    @Override
    public float[] getColorPowerRgba(LivingEntity entity) {
        List<Holder<ConfiguredPower<ColorConfiguration, ModelColorPower>>> modelColorPowers = IPowerContainer.getPowers(entity, ApoliPowers.MODEL_COLOR.get());
        if (modelColorPowers.size() > 0) {
            float red = modelColorPowers.stream().map(holder -> holder.get().getConfiguration().red()).reduce((a, b) -> a * b).get();
            float green = modelColorPowers.stream().map(holder -> holder.get().getConfiguration().green()).reduce((a, b) -> a * b).get();
            float blue = modelColorPowers.stream().map(holder -> holder.get().getConfiguration().blue()).reduce((a, c) -> a * c).get();
            float alpha = modelColorPowers.stream().map(holder -> holder.get().getConfiguration().alpha()).min(Float::compare).get();
            return new float[] { red, green, blue, alpha };
        }
        return new float[] { 1.0F, 1.0F, 1.0F, 1.0F };
    }

    @Override
    public void updateKeys(SerializableData.Instance data, Player player) {
        if (player.level().isClientSide() && !(player.isLocalPlayer())) return;

        player.getCapability(KeyPressCapability.INSTANCE).ifPresent(cap -> {
            IActivePower.Key key = data.get("key");
            if (!cap.getKeysToCheck().contains(key)) {
                cap.addKeyToCheck(key);
                if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    ApugliPacketHandler.sendS2C(new AddKeyToCheckPacket(serverPlayer.getId(), key), serverPlayer);
                }
            }
        });
    }

    @Override
    public boolean isCurrentlyUsingKey(SerializableData.Instance data, Player player) {
        Optional<KeyPressCapability> capability = player.getCapability(KeyPressCapability.INSTANCE).resolve();
        if (capability.isPresent()) {
            IActivePower.Key key = data.get("key");
            return capability.get().getCurrentlyUsedKeys().stream().anyMatch(otherKey -> ActiveKeyUtil.equals(key, otherKey));
        }
        return false;
    }

    @Override
    public Tuple<Integer, Integer> getHitsOnTarget(Entity actor, LivingEntity target) {
        LazyOptional<HitsOnTargetCapability> cap = target.getCapability(HitsOnTargetCapability.INSTANCE);
        if (cap.resolve().isPresent() && cap.resolve().get().getHits().containsKey(actor.getId())) {
            return cap.resolve().get().getHits().get(actor.getId());
        }
        return new Tuple<>(0, 0);
    }

    @Override
    public void setHitsOnTarget(Entity actor, Entity target, int initialChange, int initialTimerChange, ResourceOperation operation, ResourceOperation timerOperation) {
        target.getCapability(HitsOnTargetCapability.INSTANCE).resolve().ifPresent(capability -> {
            Tuple<Integer, Integer> valueTimerResetTimePair = capability.getHits().getOrDefault(actor.getId(), new Tuple<>(0, 0));

            int change = operation == ResourceOperation.SET ? initialChange : valueTimerResetTimePair.getA() + initialChange;
            int timerChange = timerOperation == ResourceOperation.SET ? initialTimerChange : valueTimerResetTimePair.getB() + initialTimerChange;

            capability.setHits(actor.getId(), change, timerChange);
            if (!(target instanceof ServerPlayer serverPlayer)) return;
            ApugliPacketHandler.sendS2CTrackingAndSelf(new SyncHitsOnTargetLessenedPacket(target.getId(), capability.getPreviousHits(), capability.getHits()), serverPlayer);
        });
    }

    @Override
    public HudRender getDefaultHudRender() {
        return HudRenderUtil.DONT_RENDER;
    }

    @Override
    public float getEntityEyeHeight(Entity entity) {
        return entity.getEyeHeightAccess(entity.getPose(), entity.getDimensions(entity.getPose()));
    }

    @Override
    public SerializableDataType<?> damageSourceDescriptionDataType() {
        return ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION;
    }

    @Override
    public DamageSource createDamageSource(DamageSources damageSources, SerializableData.Instance data, String typeFieldName, String descriptionFieldName) {
        return MiscUtil.createDamageSource(damageSources, Optional.ofNullable(data.get(descriptionFieldName)), Optional.ofNullable(data.get(typeFieldName)));
    }

    @Override
    public DamageSource createDamageSource(DamageSources damageSources, SerializableData.Instance data, Entity attacker, String typeFieldName, String descriptionFieldName) {
        return MiscUtil.createDamageSource(damageSources, Optional.ofNullable(data.get(descriptionFieldName)), Optional.ofNullable(data.get(typeFieldName)), attacker);
    }

    @Override
    public DamageSource createDamageSource(DamageSources damageSources, SerializableData.Instance data, Entity source, Entity attacker, String typeFieldName, String descriptionFieldName) {
        return MiscUtil.createDamageSource(damageSources, Optional.ofNullable(data.get(descriptionFieldName)), Optional.ofNullable(data.get(typeFieldName)), source, attacker);
    }

    @Override
    public boolean canSetPose(Player player) {
        return player.getForcedPose() == null;
    }

    @Override
    public void setForcedPlayerPose(Player player, @Nullable Pose pose) {
        player.setForcedPose(pose);
        if (player instanceof ServerPlayer serverPlayer) {
            Services.PLATFORM.sendS2CTrackingAndSelf(new ForcePlayerPosePacket(serverPlayer.getId(), pose), serverPlayer);
        }
    }

}
