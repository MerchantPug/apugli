package net.merchantpug.apugli.power;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;

public class RedirectLightningPower extends Power {
    public static final HashMap<LivingEntity, Float> STRUCK_BY_LIGHTNING_CHANCES = new HashMap<>();
    private final float chance;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<RedirectLightningPower>(
                Apugli.identifier("redirect_lightning"),
                new SerializableData()
                        .add("chance", SerializableDataTypes.FLOAT, null),
                data -> (type, entity) -> new RedirectLightningPower(type, entity, data.getFloat("chance")))
                .allowCondition();
    }

    @Override
    public void onAdded() {
        float chances = 0;
        for (RedirectLightningPower power : PowerHolderComponent.getPowers(entity, RedirectLightningPower.class)) {
            chances += power.chance;
        }
        STRUCK_BY_LIGHTNING_CHANCES.put(entity, chances);
    }

    @Override
    public void onRemoved() {
        float chances = 0;
        for (RedirectLightningPower power : PowerHolderComponent.getPowers(entity, RedirectLightningPower.class)) {
            chances += power.chance;
        }
        if (chances == 0) {
            STRUCK_BY_LIGHTNING_CHANCES.remove(entity);
        } else {
            STRUCK_BY_LIGHTNING_CHANCES.put(entity, chances);
        }
    }

    public RedirectLightningPower(PowerType<?> type, LivingEntity entity, float chance) {
        super(type, entity);
        this.chance = chance;
    }
}
