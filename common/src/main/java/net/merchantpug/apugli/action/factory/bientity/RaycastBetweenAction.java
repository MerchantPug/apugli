package net.merchantpug.apugli.action.factory.bientity;

import io.github.apace100.apoli.util.Space;
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
import net.minecraft.world.phys.Vec3;

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
        Vec3 direction = createDirectionVector(pair.getA().position(), pair.getB().position());

        //Block Hit
        BlockHitResult blockHitResult = RaycastUtil.raycastBlock(pair.getA(), distance, direction, Space.WORLD);
        HitResult.Type blockHitResultType = blockHitResult.getType();

        if (data.isPresent("block_action") && blockHitResultType == HitResult.Type.BLOCK) {
            createParticlesAtHitPos(data, pair.getA(), blockHitResult);
            onHitBlock(data, pair.getA(), blockHitResult);
        } else {
            createParticlesAtHitPos(data, pair.getA(), new EntityHitResult(pair.getB()));
        }
    }

    protected Vec3 createDirectionVector(Vec3 pos1, Vec3 pos2) {
        return new Vec3(pos2.x() - pos1.x(), pos2.y() - pos1.y(), pos2.z() - pos1.z()).normalize();
    }

    protected void createParticlesAtHitPos(SerializableData.Instance data, Entity entity, HitResult hitResult) {
        if(!data.isPresent("particle") || entity.level().isClientSide()) return;
        ParticleOptions particleEffect = data.get("particle");
        double distanceTo = hitResult.distanceTo(entity);

        for(double d = data.getDouble("spacing"); d < distanceTo; d += data.getDouble("spacing")) {
            double lerpValue = Mth.clamp(d / distanceTo, 0.0, 1.0);
            ((ServerLevel)entity.level()).sendParticles(particleEffect, Mth.lerp(lerpValue, entity.getEyePosition().x(), hitResult.getLocation().x()), Mth.lerp(lerpValue, entity.getEyePosition().y(), hitResult.getLocation().y()), Mth.lerp(lerpValue, entity.getEyePosition().z(), hitResult.getLocation().z()), 1, 0, 0, 0, 0);
        }
    }

    protected void onHitBlock(SerializableData.Instance data, Entity entity, BlockHitResult result) {
        if(!data.isPresent("block_action") || !Services.CONDITION.checkBlock(data, "block_condition", entity.level(), result.getBlockPos())) return;
        Services.ACTION.executeBlock(data,"block_action", entity.level(), result.getBlockPos(), result.getDirection());
    }

}