package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.merchantpug.apugli.mixin.CooldownPowerAccessor;
import net.minecraft.entity.LivingEntity;

public class InvertedCooldownPower extends CooldownPower {

    public InvertedCooldownPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender) {
        super(type, entity, cooldownDuration, hudRender);
    }

    @Override
    public float getProgress() {
        float time = entity.getEntityWorld().getTime() - ((CooldownPowerAccessor)this).getLastUseTime();
        return Math.min(1F, Math.max(1F - (time / (float)cooldownDuration), 0F));
    }
}
