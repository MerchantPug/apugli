package net.merchantpug.apugli.power.factory;

import net.merchantpug.apugli.platform.Services;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface BunnyHopPowerFactory<P> extends ResourcePowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return ResourcePowerFactory.getSerializableData()
            .add("increase_per_tick", SerializableDataTypes.DOUBLE, 0.000375)
            .add("tick_rate", SerializableDataTypes.INT, 10);
    }
    
    default void reset(P power, LivingEntity entity) {
        if (getValue(power, entity) != 0) {
            assign(power, entity, 0);
            sync(entity, power);
        }
    }
    
    default void onTravel(LivingEntity entity, Vec3 movementInput) {
        Services.POWER.getPowers(entity, this).forEach(power -> {
            SerializableData.Instance data = getDataFromPower(power);
            int tickRate = Math.max(1, data.getInt("tick_rate"));
            if(!entity.level.isClientSide && entity.tickCount % tickRate == 0) {
                int value = getValue(power, entity);
                if(increment(power, entity) != value) {
                    sync(entity, power);
                }
            }
            entity.moveRelative((float) data.getDouble("increase_per_tick") * getValue(power, entity), movementInput);
        });
    }

}
