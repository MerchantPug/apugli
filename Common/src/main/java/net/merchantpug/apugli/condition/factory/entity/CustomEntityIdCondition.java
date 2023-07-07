package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.entity.CustomAreaEffectCloud;
import net.merchantpug.apugli.entity.CustomProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class CustomEntityIdCondition implements IConditionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("entity_id", SerializableDataTypes.IDENTIFIER);
    }

    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        ResourceLocation id = data.getId("entity_id");

        if (entity instanceof CustomProjectile projectile) {
            return projectile.getEntityId().equals(id);
        }

        if (entity instanceof CustomAreaEffectCloud areaEffectCloud) {
            return areaEffectCloud.getEntityId().equals(id);
        }

        return false;
    }

}
