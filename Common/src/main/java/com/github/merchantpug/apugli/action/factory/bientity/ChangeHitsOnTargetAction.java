package com.github.merchantpug.apugli.action.factory.bientity;

import com.github.merchantpug.apugli.access.LivingEntityAccess;
import com.github.merchantpug.apugli.action.factory.IActionFactory;
import com.github.merchantpug.apugli.util.HitsOnTargetUtil;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ChangeHitsOnTargetAction implements IActionFactory<Tuple<Entity, Entity>> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("change", SerializableDataTypes.INT)
            .add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        if(!pair.getB().level.isClientSide ||
           !(pair.getA() instanceof LivingEntity actor) ||
           !(pair.getB() instanceof LivingEntity target) ||
           target.isDeadOrDying()
        ) return;
        int change = data.getInt("change");
        ResourceOperation operation = data.get("operation");
        Tuple<Integer, Integer> valueTimerResetTimeTriple = ((LivingEntityAccess)target).getHits().get(actor);
        switch (operation) {
            case ADD: {
                ((LivingEntityAccess)target).setHits(actor, valueTimerResetTimeTriple.getA() + change, valueTimerResetTimeTriple.getB());
            }
            case SET: {
                ((LivingEntityAccess)target).setHits(actor, change, valueTimerResetTimeTriple.getB());
            }
        }
        HitsOnTargetUtil.sendPacket(target, actor, HitsOnTargetUtil.PacketType.SET, valueTimerResetTimeTriple.getA(), valueTimerResetTimeTriple.getB());
    }

}
