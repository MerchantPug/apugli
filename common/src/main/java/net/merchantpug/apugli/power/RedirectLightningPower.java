package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

public class RedirectLightningPower extends Power {
    public static final HashMap<LivingEntity, Float> STRUCK_BY_LIGHTNING_CHANCES = new HashMap<>();
    private final float chance;

    public RedirectLightningPower(PowerType<?> type, LivingEntity entity, float chance) {
        super(type, entity);
        this.chance = chance;
    }

    @Override
    public void onAdded() {
        float chances = 0;
        for(RedirectLightningPower power : Services.POWER.getPowers(entity, ApugliPowers.REDIRECT_LIGHTNING.get())) {
            chances += power.chance;
        }
        STRUCK_BY_LIGHTNING_CHANCES.put(entity, chances);
    }

    @Override
    public void onRemoved() {
        float chances = 0;
        for(RedirectLightningPower power : Services.POWER.getPowers(entity, ApugliPowers.REDIRECT_LIGHTNING.get())) {
            chances += power.chance;
        }
        if(chances == 0) {
            STRUCK_BY_LIGHTNING_CHANCES.remove(entity);
        } else {
            STRUCK_BY_LIGHTNING_CHANCES.put(entity, chances);
        }
    }

    public static class Factory extends SimplePowerFactory<RedirectLightningPower> {

        public Factory() {
            super("redirect_lightning",
                    new SerializableData()
                            .add("chance", SerializableDataTypes.FLOAT, null),
                    data -> (type, entity) -> new RedirectLightningPower(type, entity, data.getFloat("chance")));
            allowCondition();
        }

        @Override
        public Class<RedirectLightningPower> getPowerClass() {
            return RedirectLightningPower.class;
        }

    }
}
