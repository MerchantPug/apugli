package com.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.ValueModifyingPower;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import com.github.merchantpug.apugli.Apugli;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class ModifySoulSpeedPower extends ValueModifyingPower {
    public final ConditionFactory<CachedBlockPosition>.Instance blockCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifySoulSpeedPower>(Apugli.identifier("modify_soul_speed"),
                new SerializableData()
                        .add("modifier", SerializableDataType.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataType.ATTRIBUTE_MODIFIERS, null)
                        .add("block_condition", SerializableDataType.BLOCK_CONDITION, null),
                data ->
                        (type, player) -> {
                            ModifySoulSpeedPower power = new ModifySoulSpeedPower(type, player, (ConditionFactory< CachedBlockPosition >.Instance)data.get("block_condition"));
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

    public ModifySoulSpeedPower(PowerType<?> type, PlayerEntity player, ConditionFactory<CachedBlockPosition>.Instance blockCondition) {
        super(type, player);
        this.blockCondition = blockCondition;
    }
}
