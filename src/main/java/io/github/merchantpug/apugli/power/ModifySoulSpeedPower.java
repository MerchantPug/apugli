package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.List;

public class ModifySoulSpeedPower extends ValueModifyingPower {
    public final ConditionFactory<CachedBlockPosition>.Instance blockCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifySoulSpeedPower>(Apugli.identifier("modify_soul_speed"),
                new SerializableData()
                        .add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (type, entity) -> {
                            ModifySoulSpeedPower power = new ModifySoulSpeedPower(type, entity, (ConditionFactory< CachedBlockPosition >.Instance)data.get("block_condition"));
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.getModifier("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public ModifySoulSpeedPower(PowerType<?> type, LivingEntity entity, ConditionFactory<CachedBlockPosition>.Instance blockCondition) {
        super(type, entity);
        this.blockCondition = blockCondition;
    }
}
