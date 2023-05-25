package net.merchantpug.apugli.condition.factory.damage;

import net.merchantpug.apugli.condition.factory.IConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;

public class IsMagicCondition implements IConditionFactory<Tuple<DamageSource, Float>> {
    
    @Override
    public boolean check(SerializableData.Instance data, Tuple<DamageSource, Float> damage) {
        return damage.getA().is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS) && damage.getA().is(DamageTypeTags.WITCH_RESISTANT_TO);
    }
    
}
