package net.merchantpug.apugli.condition.factory.damage;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;

public class IsMagicCondition implements IConditionFactory<Tuple<DamageSource, Float>> {
    
    @Override
    public boolean check(SerializableData.Instance data, Tuple<DamageSource, Float> damage) {
        return damage.getA().isMagic();
    }
    
}

