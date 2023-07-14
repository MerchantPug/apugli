package net.merchantpug.apugli.action.factory.bientity;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.util.RaycastUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class RaycastBetweenAction implements IActionFactory<Tuple<Entity, Entity>> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("block_action", Services.ACTION.blockDataType(), null)
                .add("block_condition", Services.CONDITION.blockDataType(), null)
                .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE)
                .add("spacing", SerializableDataTypes.DOUBLE, 0.5);
    }

    @Override
    public void execute(SerializableData.Instance data, Tuple<Entity, Entity> pair) {
        double distance = pair.getA().distanceTo(pair.getB());
        //Block Hit
        BlockHitResult blockHitResult = RaycastUtil.raycastBlock(pair.getA(), distance, data.get("direction"), data.get("space"));
        HitResult.Type blockHitResultType = blockHitResult.getType();
        //Entity Hit
        EntityHitResult entityHitResult = RaycastUtil.raycastEntity(blockHitResult, pair.getA(), distance, data.get("direction"), data.get("space"), null);
        HitResult.Type entityHitResultType = entityHitResult != null ? entityHitResult.getType() : null;

        if (data.isPresent("block_action") && blockHitResultType == HitResult.Type.BLOCK) {
            createParticlesAtHitPos(data, pair.getA(), blockHitResult);
            onHitBlock(data, pair.getA(), blockHitResult);
        } else if (entityHitResultType == HitResult.Type.ENTITY) {
            createParticlesAtHitPos(data, pair.getA(), entityHitResult);
        }
    }

    protected void createParticlesAtHitPos(SerializableData.Instance data, Entity entity, HitResult hitResult) {
        if(!data.isPresent("particle") || entity.level().isClientSide()) return;
        ParticleOptions particleEffect = data.get("particle");
        double distanceTo = hitResult.distanceTo(entity);

        for(double d = data.getDouble("spacing"); d < distanceTo; d += data.getDouble("spacing")) {
            ((ServerLevel)entity.level()).sendParticles(particleEffect, Mth.lerp(d / distanceTo, entity.getEyePosition().x(), hitResult.getLocation().x()), Mth.lerp(d / distanceTo, entity.getEyePosition().y(), hitResult.getLocation().y()), Mth.lerp(d / distanceTo, entity.getEyePosition().z(), hitResult.getLocation().z()), 1, 0, 0, 0, 0);
        }
    }

    protected void onHitBlock(SerializableData.Instance data, Entity entity, BlockHitResult result) {
        if(!data.isPresent("block_action") || !Services.CONDITION.checkBlock(data, "block_condition", entity.level(), result.getBlockPos())) return;
        Services.ACTION.executeBlock(data,"block_action", entity.level(), result.getBlockPos(), result.getDirection());
    }

}