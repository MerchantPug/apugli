package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;

public class SetApugliEntityGroupPower extends Power {
    public final EntityGroup group;

    public SetApugliEntityGroupPower(PowerType<?> type, LivingEntity player, EntityGroup group) {
        super(type, player);
        this.group = group;
    }
}
