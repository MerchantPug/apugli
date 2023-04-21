package net.merchantpug.apugli;

import net.merchantpug.apugli.capability.KeyPressCapability;
import net.merchantpug.apugli.network.ApugliPacketHandler;
import net.merchantpug.apugli.network.s2c.SyncKeyPressCapabilityPacket;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.MobsIgnorePower;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

@Mod.EventBusSubscriber(modid = Apugli.ID)
public class ApugliForgeEventHandler {

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(KeyPressCapability.IDENTIFIER, new KeyPressCapability(player));
        }
    }

    /*
    The one discrepancy from Fabric Apugli is that the action will always execute in the up direction, this should hopefully have no repercussions.
     */
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (Services.POWER.hasPower(event.getEntity(), ApugliPowers.ACTION_ON_BONEMEAL.get()) && event.getBlock().getBlock() instanceof BonemealableBlock bonemeal
                && bonemeal.isValidBonemealTarget(event.getLevel(), event.getPos(), event.getBlock(), event.getLevel().isClientSide) && event.getLevel() instanceof ServerLevel)
            if (bonemeal.isBonemealSuccess(event.getLevel(), event.getLevel().random, event.getPos(), event.getBlock())) {
                Services.POWER.getPowers(event.getEntity(), ApugliPowers.ACTION_ON_BONEMEAL.get())
                        .stream()
                        .filter(p -> p.doesApply(new BlockInWorld(event.getLevel(), event.getPos(), true)))
                        .forEach(p -> p.executeActions(event.getLevel(), event.getPos(), Direction.UP));
            }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().getCapability(KeyPressCapability.INSTANCE).isPresent())
            event.getEntity().getCapability(KeyPressCapability.INSTANCE).ifPresent(KeyPressCapability::tick);
    }

    @SubscribeEvent
    public static void onStartPlayerTrack(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && event.getTarget() instanceof LivingEntity target)
            target.getCapability(KeyPressCapability.INSTANCE).ifPresent(capability -> ApugliPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncKeyPressCapabilityPacket(target.getId(), capability.getKeysToCheck(), capability.getCurrentlyUsedKeys())));
    }

    @SubscribeEvent
    public static void onMobTargetChange(LivingChangeTargetEvent event) {
        if(event.getEntity().level.isClientSide()) return;

        List<MobsIgnorePower> powers = Services.POWER.getPowers(event.getOriginalTarget(), ApugliPowers.MOBS_IGNORE.get());
        if (powers.stream().anyMatch(power -> power.shouldIgnore(event.getEntity())))
            event.setCanceled(true);
    }

}
