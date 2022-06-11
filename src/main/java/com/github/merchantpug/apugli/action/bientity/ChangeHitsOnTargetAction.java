package com.github.merchantpug.apugli.action.bientity;

import com.github.merchantpug.apugli.Apugli;
import com.github.merchantpug.apugli.access.LivingEntityAccess;
import com.github.merchantpug.apugli.util.HitsOnTargetUtil;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

public class ChangeHitsOnTargetAction {
    public static void action(SerializableData.Instance data, Pair<Entity, Entity> pair) {
        if (!pair.getRight().world.isClient || !(pair.getLeft() instanceof LivingEntity) || !(pair.getRight() instanceof LivingEntity) || ((LivingEntity) pair.getRight()).isDead()) return;
        int change = data.getInt("change");
        ResourceOperation operation = data.get("operation");
        Pair<Integer, Integer> valueTimerResetTimeTriple = ((LivingEntityAccess)pair.getRight()).getHits().get(pair.getLeft());
        switch (operation) {
            case ADD: {
                ((LivingEntityAccess)pair.getRight()).setHits(pair.getLeft(), valueTimerResetTimeTriple.getLeft() + change, valueTimerResetTimeTriple.getRight());
            }
            case SET: {
                ((LivingEntityAccess)pair.getRight()).setHits(pair.getLeft(), change, valueTimerResetTimeTriple.getRight());
            }
        }
        HitsOnTargetUtil.sendPacket((LivingEntity)pair.getRight(), (LivingEntity)pair.getLeft(), HitsOnTargetUtil.PacketType.SET, valueTimerResetTimeTriple.getLeft(), valueTimerResetTimeTriple.getRight());
    }

    public static ActionFactory<Pair<Entity, Entity>> getFactory() {
        return new ActionFactory<>(Apugli.identifier("change_hits_on_target"), new SerializableData()
                .add("change", SerializableDataTypes.INT)
                .add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
                ChangeHitsOnTargetAction::action
        );
    }
}
