package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.player.PlayerEntity;

public class AerialAffinityPower extends Power {
    public AerialAffinityPower(PowerType<?> type, PlayerEntity player) {
        super(type, player);
    }


}
