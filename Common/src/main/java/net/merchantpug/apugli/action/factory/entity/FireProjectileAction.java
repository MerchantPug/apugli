package net.merchantpug.apugli.action.factory.entity;

import net.merchantpug.apugli.action.factory.IActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class FireProjectileAction implements IActionFactory<Entity> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("count", SerializableDataTypes.INT, 1)
            .add("speed", SerializableDataTypes.FLOAT, 1.5F)
            .add("divergence", SerializableDataTypes.FLOAT, 1F)
            .add("sound", SerializableDataTypes.SOUND_EVENT, null)
            .add("entity_type", SerializableDataTypes.ENTITY_TYPE)
            .add("tag", SerializableDataTypes.NBT, null)
            .add("bientity_action", Services.ACTION.biEntityDataType(), null);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        if(data.isPresent("sound")) {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), data.get("sound"), SoundSource.NEUTRAL, 0.5F, 0.4F / (((LivingEntity) entity).getRandom().nextFloat() * 0.4F + 0.8F));
        }
        EntityType<?> type = data.get("entity_type");
        float speed = data.getFloat("speed");
        float divergence = data.getFloat("divergence") * 0.075F;
        CompoundTag tag = data.get("tag");
        for(int i = 0; i < data.getInt("count"); ++i) {
            Entity result = createProjectile((LivingEntity) entity, type, speed, divergence, tag);
            if(result != null) {
                entity.level().addFreshEntity(result);
                Services.ACTION.executeBiEntity(data, "bientity_action", entity, result);
            }
        }
    }
    
    @Nullable
    protected Entity createProjectile(LivingEntity actor, EntityType<?> type, float speed, float divergence, @Nullable CompoundTag tag) {
        Entity result = type.create(actor.level());
        if(result == null) return null;
        Vec3 rotationVec = actor.getLookAngle();
        Vec3 spawnPos = actor.position().add(0.0D, actor.getEyeHeight(), 0.0D).add(rotationVec);
        result.moveTo(spawnPos.x(), spawnPos.y(), spawnPos.z(), actor.getXRot(), actor.getYRot());
        
        if(result instanceof Projectile projectile) {
            if(result instanceof AbstractHurtingProjectile abstractHurtingProjectile) {
                abstractHurtingProjectile.xPower = rotationVec.x * speed;
                abstractHurtingProjectile.yPower = rotationVec.y * speed;
                abstractHurtingProjectile.zPower = rotationVec.z * speed;
            }
            projectile.setOwner(actor);
            projectile.shootFromRotation(actor, actor.getXRot(), actor.getYRot(), 0.0F, speed, divergence);
        } else {
            float f = -Mth.sin(actor.getYRot() * 0.017453292F) * Mth.cos(actor.getXRot() * 0.017453292F);
            float g = -Mth.sin(actor.getXRot() * 0.017453292F);
            float h = Mth.cos(actor.getYRot() * 0.017453292F) * Mth.cos(actor.getXRot() * 0.017453292F);
            Vec3 divergenceVec = new Vec3(f, g, h).normalize()
                .add(
                    actor.getRandom().nextGaussian() * divergence,
                    actor.getRandom().nextGaussian() * divergence,
                    actor.getRandom().nextGaussian() * divergence
                )
                .scale(speed);
            result.setDeltaMovement(divergenceVec);
            Vec3 entityVec = actor.getDeltaMovement();
            if(actor.onGround()) entityVec = entityVec.multiply(1, 0, 1);
            result.setDeltaMovement(result.getDeltaMovement().add(entityVec));
        }

        if(tag != null) {
            CompoundTag mergedTag = result.saveWithoutId(new CompoundTag());
            mergedTag.merge(tag);
            result.load(mergedTag);
        }
        return result;
    }

}