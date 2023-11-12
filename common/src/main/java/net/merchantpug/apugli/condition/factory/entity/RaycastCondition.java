package net.merchantpug.apugli.condition.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Space;
import net.merchantpug.apugli.condition.factory.IConditionFactory;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.util.RaycastUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class RaycastCondition implements IConditionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("distance", SerializableDataTypes.DOUBLE, null)
                .add("direction", SerializableDataTypes.VECTOR, null)
                .add("space", ApoliDataTypes.SPACE, Space.WORLD)
                .add("block_condition", Services.CONDITION.blockDataType(), null)
                .add("target_condition", Services.CONDITION.entityDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }
    
    @Override
    public boolean check(SerializableData.Instance data, Entity entity) {
        //Block Hit
        double blockDistance = data.isPresent("distance") ?
            data.getDouble("distance") :
            Services.PLATFORM.getReachDistance(entity);
        BlockHitResult blockHitResult = RaycastUtil.raycastBlock(entity, blockDistance, data.get("direction"), data.get("space"));
        HitResult.Type blockHitResultType = blockHitResult.getType();
        //Entity Hit
        double entityDistance = data.isPresent("distance") ?
            data.getDouble("distance") :
            Services.PLATFORM.getAttackRange(entity);
        EntityHitResult entityHitResult = RaycastUtil.raycastEntity(blockHitResult, entity, entityDistance);
        HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;
        //Check Conditions
        if(entityHitResultType == HitResult.Type.ENTITY) return checkHitEntity(data, entity, entityHitResult);
        if(blockHitResultType == HitResult.Type.BLOCK) return checkHitBlock(data, entity, blockHitResult);
        return false;
    }

    protected boolean checkHitBlock(SerializableData.Instance data, Entity entity, BlockHitResult result) {
        return Services.CONDITION.checkBlock(data, "block_condition", entity.level(), result.getBlockPos());
    }

    protected boolean checkHitEntity(SerializableData.Instance data, Entity entity, EntityHitResult result) {
        return Services.CONDITION.checkEntity(data, "target_condition", entity)
            && Services.CONDITION.checkBiEntity(data, "bientity_condition", entity, result.getEntity());
    }

}
