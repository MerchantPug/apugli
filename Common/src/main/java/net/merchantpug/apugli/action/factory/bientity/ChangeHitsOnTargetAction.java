package net.merchantpug.apugli.action.factory.bientity;

import net.merchantpug.apugli.action.factory.IActionFactory;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
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
        if (!pair.getB().level.isClientSide || !(pair.getA() instanceof LivingEntity) || !(pair.getB() instanceof LivingEntity) || ((LivingEntity) pair.getB()).isDeadOrDying()) return;
        int change = data.getInt("change");
        int timerChange = data.getInt("timer_change");

        ResourceOperation operation = data.isPresent("change") ? data.get("operation") : ResourceOperation.ADD;
        ResourceOperation timerOperation =  data.isPresent("timer_change") ? data.get("timer_operation") : ResourceOperation.ADD;

        Services.PLATFORM.setHitsOnTarget(pair.getA(), pair.getB(), change, timerChange, operation, timerOperation);
    }

}
