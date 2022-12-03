package net.merchantpug.apugli.action.bientity;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.component.ApugliEntityComponents;
import net.merchantpug.apugli.component.HitsOnTargetComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

public class ChangeHitsOnTargetAction {
    public static void action(SerializableData.Instance data, Pair<Entity, Entity> pair) {
        if (!pair.getRight().world.isClient || !(pair.getLeft() instanceof LivingEntity) || !(pair.getRight() instanceof LivingEntity) || ((LivingEntity) pair.getRight()).isDead()) return;
        int initialChange = data.getInt("change");
        int initialTimerChange = data.getInt("timer_change");
        ResourceOperation operation = data.isPresent("change") ? data.get("operation") : ResourceOperation.ADD;
        ResourceOperation timerOperation =  data.isPresent("timer_change") ? data.get("timer_operation") : ResourceOperation.ADD;

        HitsOnTargetComponent component = ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.get(pair.getRight());
        Pair<Integer, Integer> valueTimerResetTimePair = component.getHits().getOrDefault(pair.getLeft().getId(), new Pair<>(0, 0));

        int change = operation == ResourceOperation.SET ? initialChange : valueTimerResetTimePair.getLeft() + initialChange;
        int timerChange = timerOperation == ResourceOperation.SET ? initialTimerChange : valueTimerResetTimePair.getRight() + initialTimerChange;

        component.setHits(pair.getLeft(), change, timerChange);
        ApugliEntityComponents.HITS_ON_TARGET_COMPONENT.sync(pair.getRight());
    }

    public static ActionFactory<Pair<Entity, Entity>> getFactory() {
        return new ActionFactory<>(Apugli.identifier("change_hits_on_target"), new SerializableData()
                .add("change", SerializableDataTypes.INT, null)
                .add("timer_change", SerializableDataTypes.INT, null)
                .add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD)
                .add("timer_operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD),
                ChangeHitsOnTargetAction::action
        );
    }
}
