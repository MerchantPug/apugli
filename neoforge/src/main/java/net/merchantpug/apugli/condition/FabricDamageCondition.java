package net.merchantpug.apugli.condition;

import net.merchantpug.apugli.condition.configuration.FabricConditionConfiguration;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.factory.DamageCondition;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class FabricDamageCondition extends DamageCondition<FabricConditionConfiguration<Tuple<DamageSource, Float>>> {
    
    public FabricDamageCondition(SerializableData data, BiPredicate<SerializableData.Instance, Tuple<DamageSource, Float>> condition) {
        super(FabricConditionConfiguration.codec(data, condition));
    }
    
    @Override
    protected boolean check(FabricConditionConfiguration<Tuple<DamageSource, Float>> config, DamageSource source, float amount) {
        return config.condition().test(new Tuple<>(source, amount));
    }
    
}
