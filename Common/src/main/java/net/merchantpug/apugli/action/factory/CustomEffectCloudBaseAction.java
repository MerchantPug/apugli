package net.merchantpug.apugli.action.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.entity.CustomAreaEffectCloud;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.AreaEffectCloudEntityAccessor;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class CustomEffectCloudBaseAction {

    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("entity_id", SerializableDataTypes.IDENTIFIER)
                .add("radius", SerializableDataTypes.FLOAT, 3.0F)
                .add("radius_on_use", SerializableDataTypes.FLOAT, 0.5F)
                .add("radius_per_tick", SerializableDataTypes.FLOAT, 0.03F)
                .add("wait_time", SerializableDataTypes.INT, 10)
                .add("duration", SerializableDataTypes.INT, 600)
                .add("reapplication_delay", SerializableDataTypes.INT, 20)
                .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
                .add("powers_to_apply", SerializableDataType.list(Services.POWER.getPowerTypeDataType()), null)
                .add("owner_cloud_bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("cloud_target_bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("owner_target_bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("owner_target_bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }

    public CustomAreaEffectCloud createCloud(SerializableData.Instance data, Entity owner, double x, double y, double z) {
        CustomAreaEffectCloud cloud = new CustomAreaEffectCloud(owner.level(), x, y, z);
        if (owner instanceof LivingEntity living)
            cloud.setOwner(living);
        cloud.setEntityId(data.getId("entity_id"));
        cloud.setRadius(data.getFloat("radius"));
        cloud.setRadiusOnUse(data.getFloat("radius_on_use"));
        cloud.setWaitTime(data.getInt("wait_time"));
        cloud.setDuration(data.getInt("duration"));
        cloud.setRadiusPerTick(data.getFloat("radius_per_tick"));
        cloud.setOwnerCloudBiEntityAction(data, "owner_cloud_bientity_action");
        cloud.setCloudTargetBiEntityAction(data, "cloud_target_bientity_action");
        cloud.setOwnerTargetBiEntityAction(data, "owner_target_bientity_action");
        cloud.setParticle(data.get("particle"));
        cloud.setBiEntityCondition(data, "bientity_condition");
        cloud.setOwnerTargetBiEntityCondition(data, "owner_target_bientity_condition");
        ((AreaEffectCloudEntityAccessor)cloud).setReapplicationDelay(data.getInt("reapplication_delay"));
        if (data.isPresent("powers_to_apply")) {
            data.<List<?>>get("powers_to_apply").forEach(cloud::addPowerToApply);
        }
        return cloud;
    }

}
