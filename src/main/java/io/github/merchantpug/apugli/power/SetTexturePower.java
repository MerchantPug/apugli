package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class SetTexturePower extends Power {
    public final Identifier textureLocation;

    public SetTexturePower(PowerType<?> type, LivingEntity entity, Identifier textureLocation) {
        super(type, entity);
        this.textureLocation = textureLocation;
    }
}
