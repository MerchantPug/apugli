package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.CooldownPower;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.HudRender;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.mixin.CooldownPowerAccessor;
import net.minecraft.entity.player.PlayerEntity;

public class InvertedCooldownPower extends CooldownPower {
    public InvertedCooldownPower(PowerType<?> type, PlayerEntity player, int cooldownDuration, HudRender hudRender) {
        super(type, player, cooldownDuration, hudRender);
    }

    @Override
    public float getProgress() {
        float time = player.getEntityWorld().getTime() - ((CooldownPowerAccessor)this).getLastUseTime();
        return Math.min(1F, Math.max(1F - (time / (float)cooldownDuration), 0F));
    }

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<>(Apugli.identifier("inverted_cooldown"),
                new SerializableData()
                        .add("cooldown", SerializableDataType.INT)
                        .add("hud_render", SerializableDataType.HUD_RENDER),
                (data) ->
                        (type, player) -> {
                return new InvertedCooldownPower(type, player, data.getInt("cooldown"), (HudRender)data.get("hud_render"));
            })
        .allowCondition();
    }
}
