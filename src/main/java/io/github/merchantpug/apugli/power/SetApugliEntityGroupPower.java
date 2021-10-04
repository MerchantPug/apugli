package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;

public class SetApugliEntityGroupPower extends Power {
    public final EntityGroup group;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<SetApugliEntityGroupPower>(Apugli.identifier("entity_group"),
                new SerializableData()
                        .add("group", ApugliDataTypes.APUGLI_ENTITY_GROUP),
                data ->
                        (type, entity) ->
                                new SetApugliEntityGroupPower(type, entity, (EntityGroup)data.get("group")))
                .allowCondition();
    }

    public SetApugliEntityGroupPower(PowerType<?> type, LivingEntity player, EntityGroup group) {
        super(type, player);
        this.group = group;
    }
}
