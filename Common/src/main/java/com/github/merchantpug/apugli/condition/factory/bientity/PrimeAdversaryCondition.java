package com.github.merchantpug.apugli.condition.factory.bientity;

import com.github.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class PrimeAdversaryCondition implements IConditionFactory<Tuple<Entity, Entity>> {
    
    public boolean check(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        return pair.getB() instanceof LivingEntity target &&
            target.getKillCredit() != null &&
            target.getKillCredit().equals(pair.getA());
    }

}
