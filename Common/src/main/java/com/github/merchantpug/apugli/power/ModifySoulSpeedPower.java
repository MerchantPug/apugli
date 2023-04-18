package net.merchantpug.apugli.power;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/ModifySoulSpeedPower.java
import net.merchantpug.apugli.Apugli;
========
import the.great.migration.merchantpug.apugli.Apugli;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/ModifySoulSpeedPower.java
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.calio.data.SerializableData;
<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/ModifySoulSpeedPower.java
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
========
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/ModifySoulSpeedPower.java

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class ModifySoulSpeedPower extends ValueModifyingPower {
    public final ConditionFactory<BlockInWorld>.Instance blockCondition;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifySoulSpeedPower>(Apugli.identifier("modify_soul_speed"),
                new SerializableData()
                        .add("modifier", Modifier.DATA_TYPE, null)
                        .add("modifiers", Modifier.LIST_TYPE, null)
                        .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
                data ->
                        (type, entity) -> {
                            ModifySoulSpeedPower power = new ModifySoulSpeedPower(type, entity, (ConditionFactory< BlockInWorld >.Instance)data.get("block_condition"));
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.get("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<Modifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            return power;
                        })
                .allowCondition();
    }

    public ModifySoulSpeedPower(PowerType<?> type, LivingEntity entity, ConditionFactory<BlockInWorld>.Instance blockCondition) {
        super(type, entity);
        this.blockCondition = blockCondition;
    }
}
