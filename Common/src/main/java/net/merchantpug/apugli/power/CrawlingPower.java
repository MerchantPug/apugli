package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class CrawlingPower extends Power {
    private static final Map<Entity, Boolean> WAS_ACTIVE = new HashMap<>();

    public CrawlingPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public static void tickOnceForge(Player player) {
        if (!WAS_ACTIVE.containsKey(player)) return;

        if (player.isFallFlying() || player.isSleeping() || player.isAutoSpinAttack()) {
            if (wasActive(player)) {
                Services.PLATFORM.setForcedPlayerPose(player, null);
                setWasActive(player, false);
            }
            return;
        }

        if (!wasActive(player) && Services.PLATFORM.canSetPose(player) && Services.POWER.hasPower(player, ApugliPowers.CRAWLING.get()) && (player.hasPose(Pose.STANDING) || player.hasPose(Pose.CROUCHING))) {
            Services.PLATFORM.setForcedPlayerPose(player, Pose.SWIMMING);
            setWasActive(player, true);
        } else if (wasActive(player) && !Services.POWER.hasPower(player, ApugliPowers.CRAWLING.get())) {
            Services.PLATFORM.setForcedPlayerPose(player, null);
            setWasActive(player, false);
        }
    }

    @Override
    public void onAdded() {
        if (this.entity instanceof Player player && !WAS_ACTIVE.containsKey(player)) {
            WAS_ACTIVE.put(player, false);
        }
    }

    @Override
    public void onRemoved() {
        if (this.entity instanceof Player player && Services.POWER.getPowers(player, ApugliPowers.CRAWLING.get()).size() - 1 <= 0) {
            boolean wasActive = WAS_ACTIVE.remove(player);
            if (wasActive) {
                Services.PLATFORM.setForcedPlayerPose(player, null);
            }
        }
    }

    private static void setWasActive(Player player, boolean value) {
        WAS_ACTIVE.put(player, value);
    }

    public static boolean wasActive(Player player) {
        return WAS_ACTIVE.get(player);
    }

    public static class Factory extends SimplePowerFactory<CrawlingPower> {

        public Factory() {
            super("crawling",
                    new SerializableData(),
                    data -> CrawlingPower::new);
            allowCondition();
        }

        @Override
        public Class<CrawlingPower> getPowerClass() {
            return CrawlingPower.class;
        }

    }

}
