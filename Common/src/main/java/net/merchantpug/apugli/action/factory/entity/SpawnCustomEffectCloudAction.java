package net.merchantpug.apugli.action.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.action.factory.CustomEffectCloudBaseAction;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.entity.CustomAreaEffectCloud;
import net.minecraft.world.entity.Entity;

public class SpawnCustomEffectCloudAction extends CustomEffectCloudBaseAction implements IActionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return super.getSerializableData();
    }

    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        CustomAreaEffectCloud cloud = createCloud(data, entity, entity.getX(), entity.getY(), entity.getZ());
        entity.level.addFreshEntity(cloud);
    }

}
