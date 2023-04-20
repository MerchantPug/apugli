package net.merchantpug.apugli.action.factory.entity;

import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.ResourceOperation;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.OptionalInt;

public class ResourceTransferAction implements IActionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("resource", Services.POWER.getPowerTypeDataType())
            .add("provider", Services.POWER.getPowerTypeDataType())
            .add("operation", ApoliDataTypes.RESOURCE_OPERATION);
    }
    
    public void execute(SerializableData.Instance data, Entity entity) {
        if(!(entity instanceof LivingEntity living)) return;
        OptionalInt resource = Services.POWER.getResource(living, data, "resource");
        if(resource.isEmpty()) return;
        OptionalInt provider = Services.POWER.getResource(living, data, "provider");
        if(provider.isEmpty()) return;
        int result;
        switch((ResourceOperation)data.get("operation")) {
            case ADD -> result = resource.getAsInt() + provider.getAsInt();
            case SET -> result = provider.getAsInt();
            default -> throw new IllegalArgumentException("Unexpected Resource Operation, should only be ADD or SET!");
        }
        Services.POWER.setResource(living, data, "resource", result);
    }

}
