package io.github.merchantpug.apugli.action.bientity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.access.LivingEntityAccess;
import io.github.merchantpug.apugli.util.HitsOnTargetUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

import java.util.function.Consumer;

public class ChangeHitsOnTargetAction {
    public static void action(SerializableData.Instance data, Pair<Entity, Entity> pair) {
        if (pair.getRight().world.isClient || !(pair.getLeft() instanceof LivingEntity) || !(pair.getRight() instanceof LivingEntity) || ((LivingEntity) pair.getRight()).isDead()) return;
        int change = data.getInt("change");
        ResourceOperation operation = data.get("operation");
        switch (operation) {
            case ADD: {
                ((LivingEntityAccess)pair.getRight()).addToHits(pair.getLeft(), change);
                HitsOnTargetUtil.sendPacket((LivingEntity)pair.getRight(), (LivingEntity)pair.getLeft(), HitsOnTargetUtil.PacketType.SET, ((LivingEntityAccess)pair.getRight()).getHits().get(pair.getLeft()));
            }
            case SET: {
                ((LivingEntityAccess)pair.getRight()).setHits(pair.getLeft(), change);
                HitsOnTargetUtil.sendPacket((LivingEntity)pair.getRight(), (LivingEntity)pair.getLeft(), HitsOnTargetUtil.PacketType.REMOVE, 0);
            }
        }
    }

    public static ActionFactory<Pair<Entity, Entity>> getFactory() {
        return new ActionFactory<>(Apugli.identifier("change_hits_on_target"), new SerializableData()
                .add("change", SerializableDataTypes.INT)
                .add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
                ChangeHitsOnTargetAction::action
        );
    }
}
