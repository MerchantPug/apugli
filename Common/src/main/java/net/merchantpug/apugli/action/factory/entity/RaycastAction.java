package net.merchantpug.apugli.action.factory.entity;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Space;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.util.RaycastUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class RaycastAction implements IActionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("distance", SerializableDataTypes.DOUBLE, null)
                .add("direction", SerializableDataTypes.VECTOR, null)
                .add("space", ApoliDataTypes.SPACE, Space.WORLD)
                .add("pierce", SerializableDataTypes.BOOLEAN, false)
                .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, null)
                .add("spacing", SerializableDataTypes.DOUBLE, 0.5)
                .add("block_action", Services.ACTION.blockDataType(), null)
                .add("block_condition", Services.CONDITION.blockDataType(), null)
                .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("target_action", Services.ACTION.entityDataType(), null)
                .add("target_condition", Services.CONDITION.entityDataType(), null)
                .add("self_action", Services.ACTION.entityDataType(), null);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
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
        EntityHitResult entityHitResult = RaycastUtil.raycastEntity(blockHitResult, entity, entityDistance, data.get("direction"), data.get("space"), null);
        HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;
        //Execute Actions
        if(data.getBoolean("pierce")) {
            List<EntityHitResult> list = RaycastUtil.raycastEntities(entity, (traceEntity) -> !traceEntity.isSpectator() && traceEntity.isPickable(), entityDistance, data.get("direction"), data.get("space"));
            handlePierce(data, entity, list);
            return;
        }
        if(entityHitResultType == HitResult.Type.ENTITY) {
            createParticlesAtHitPos(data, entity, entityHitResult);
            onHitEntity(data, entity, entityHitResult, false);
            return;
        }
        if(blockHitResultType == HitResult.Type.BLOCK) {
            createParticlesAtHitPos(data, entity, blockHitResult);
            onHitBlock(data, entity, blockHitResult);
        }
    }
    
    protected void createParticlesAtHitPos(SerializableData.Instance data, Entity entity, HitResult hitResult) {
        if(!data.isPresent("particle") || entity.level().isClientSide()) return;
        ParticleOptions particleEffect = data.get("particle");
        double distanceTo = hitResult.distanceTo(entity);
        
        for(double d = data.getDouble("spacing"); d < distanceTo; d += data.getDouble("spacing")) {
            ((ServerLevel)entity.level()).sendParticles(particleEffect, Mth.lerp(d / distanceTo, entity.getX(), hitResult.getLocation().x()), Mth.lerp(d / distanceTo, entity.getY(), hitResult.getLocation().y()), Mth.lerp(d / distanceTo, entity.getZ(), hitResult.getLocation().z()), 1, 0, 0, 0, 0);
        }
    }
    
    protected void handlePierce(SerializableData.Instance data, Entity entity, List<EntityHitResult> list) {
        if(list.isEmpty()) return;

        Entity previousEntity = entity;
        for (EntityHitResult result : list) {
            createParticlesAtHitPos(data, previousEntity, result);
            previousEntity = result.getEntity();
            onHitEntity(data, entity, result, true);
        }
        executeSelfAction(data, entity);
    }

    protected void executeSelfAction(SerializableData.Instance data, Entity entity) {
        if(!data.isPresent("self_action") || !entity.isAlive()) return;
        Services.ACTION.executeEntity(data,"self_action", entity);
    }

    protected void onHitBlock(SerializableData.Instance data, Entity entity, BlockHitResult result) {
        if(!data.isPresent("block_action") || !Services.CONDITION.checkBlock(data, "block_condition", entity.level(), result.getBlockPos())) return;
        Services.ACTION.executeBlock(data,"block_action", entity.level(), result.getBlockPos(), result.getDirection());
        executeSelfAction(data, entity);
    }
    
    protected void onHitEntity(SerializableData.Instance data, Entity actor, EntityHitResult result, boolean calledThroughPierce) {
        boolean hasTargetAction = data.isPresent("target_action");
        boolean hasBiEntityAction = data.isPresent("bientity_action");
        if(Services.CONDITION.checkEntity(data, "target_condition", actor)) return;
        Entity target = result.getEntity();
        if(!Services.CONDITION.checkBiEntity(data, "bientity_condition", actor, target)) return;
        if(hasTargetAction) {
            Services.ACTION.executeEntity(data, "target_action", actor);
        }
        if(hasBiEntityAction) {
            Services.ACTION.executeBiEntity(data, "bientity_action", actor, target);
        }

        if(calledThroughPierce) return;
        executeSelfAction(data, actor);
    }

}
