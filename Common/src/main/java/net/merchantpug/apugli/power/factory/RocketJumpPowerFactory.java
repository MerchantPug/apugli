package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Predicate;

@Deprecated
public interface RocketJumpPowerFactory<P> extends ActiveCooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ActiveCooldownPowerFactory.getSerializableData()
                .add("distance", SerializableDataTypes.DOUBLE, Double.NaN)
                .add("source", SerializableDataTypes.DAMAGE_SOURCE, null)
                .add("amount", SerializableDataTypes.FLOAT, 0.0F)
                .add("velocity", SerializableDataTypes.DOUBLE, 1.0D)
                .addFunctionedDefault("horizontal_velocity", SerializableDataTypes.DOUBLE, data -> data.getDouble("velocity"))
                .addFunctionedDefault("vertical_velocity", SerializableDataTypes.DOUBLE, data -> data.getDouble("velocity"))
                .add("velocity_clamp_multiplier", SerializableDataTypes.DOUBLE, 1.8D)
                .add("use_charged", SerializableDataTypes.BOOLEAN, false)
                .add("charged_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("charged_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("water_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("water_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("damage_modifier", Services.PLATFORM.getModifierDataType(), null)
                .add("damage_modifiers", Services.PLATFORM.getModifiersDataType(), null)
                .add("targetable_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("damage_bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }

    List<?> chargedModifiers(P power, Entity entity);

    List<?> waterModifiers(P power, Entity entity);

    List<?> damageModifiers(P power, Entity entity);

}
