package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;

public class ApugliDamageConditions {
    public static void register() {
        register(new ConditionFactory<>(Apugli.identifier("explosive"), new SerializableData(),
                (data, dmg) -> dmg.getLeft().isExplosive()));
        register(new ConditionFactory<>(Apugli.identifier("magic"), new SerializableData(),
                (data, dmg) -> dmg.getLeft().isMagic()));
    }

    private static void register(ConditionFactory<Pair<DamageSource, Float>> conditionFactory) {
        Registry.register(ApoliRegistries.DAMAGE_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
}
