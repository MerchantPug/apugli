package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.merchantpug.apugli.util.PlayerModelType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class SetTexturePower extends Power {
    public final Identifier textureLocation;
    public final PlayerModelType model;

    public SetTexturePower(PowerType<?> type, LivingEntity entity, Identifier textureLocation, PlayerModelType model) {
        super(type, entity);
        this.textureLocation = textureLocation;
        this.model = model;
    }
}
