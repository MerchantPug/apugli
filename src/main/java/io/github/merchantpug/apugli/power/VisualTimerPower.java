package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.HudRender;
import net.minecraft.entity.LivingEntity;

public class VisualTimerPower extends InvertedCooldownPower {
    private final boolean resetOnRespawn;

    public VisualTimerPower(PowerType<?> type, LivingEntity player, int cooldownDuration, HudRender hudRender, boolean resetOnRespawn) {
        super(type, player, cooldownDuration, hudRender);
        this.resetOnRespawn = resetOnRespawn;
    }

    @Override
    public void onGained() {
        super.onGained();
        this.use();
    }

    @Override
    public void onRespawn() {
        if (resetOnRespawn) {
            this.use();
        }
    }
}
