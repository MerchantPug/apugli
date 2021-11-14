package io.github.merchantpug.apugli.action.entity;


import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FireProjectileAction {
    public static void action(SerializableData.Instance data, Entity entity) {
        if (data.isPresent("sound")) {
            entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent) data.get("sound"), SoundCategory.NEUTRAL, 0.5F, 0.4F / (((LivingEntity) entity).getRandom().nextFloat() * 0.4F + 0.8F));
        }
        for (int i = 0; i < data.getInt("count"); ++i) {
            fireProjectile(data, (LivingEntity) entity);
        }
    }
    
    private static void fireProjectile(SerializableData.Instance data, LivingEntity entity) {
        if (!data.isPresent("entity_type")) return;
        Entity firedEntity = ((EntityType<?>)data.get("entity_type")).create(entity.world);
        if (firedEntity == null) return;

        Vec3d rotationVector = entity.getRotationVector();
        Vec3d spawnPos = entity.getPos().add(0.0D, entity.getStandingEyeHeight(), 0.0D).add(rotationVector);
        firedEntity.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), entity.getPitch(), entity.getYaw());

        if (firedEntity instanceof ProjectileEntity) {
            if (firedEntity instanceof ExplosiveProjectileEntity abstractHurtingProjectile) {
                abstractHurtingProjectile.powerX = rotationVector.x * data.getDouble("speed");
                abstractHurtingProjectile.powerY = rotationVector.y * data.getDouble("speed");
                abstractHurtingProjectile.powerZ = rotationVector.z * data.getDouble("speed");
            }

            ProjectileEntity projectile = (ProjectileEntity) firedEntity;
            projectile.setOwner(entity);
            projectile.setProperties(entity, entity.getPitch(), entity.getYaw(), 0.0F, data.getFloat("speed"), data.getFloat("divergence"));
        } else {
            float f = -MathHelper.sin(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);
            float g = -MathHelper.sin(entity.getPitch() * 0.017453292F);
            float h = MathHelper.cos(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);

            Vec3d vec3d = new Vec3d(f, g, h).normalize()
                    .add(
                            entity.getRandom().nextGaussian() * 0.007499999832361937D * data.getDouble("divergence"),
                            entity.getRandom().nextGaussian() * 0.007499999832361937D * data.getDouble("divergence"),
                            entity.getRandom().nextGaussian() * 0.007499999832361937D * data.getDouble("divergence")
                    ).multiply(data.getDouble("speed"));
            firedEntity.setVelocity(vec3d);
            Vec3d entityVelo = entity.getVelocity();
            firedEntity.setVelocity(firedEntity.getVelocity().add(entityVelo.x, entity.isOnGround() ? 0.0D : entityVelo.y, entityVelo.z));
        }

        if (data.isPresent("tag")) {
            NbtCompound mergedTag = firedEntity.writeNbt(new NbtCompound());
            mergedTag.copyFrom((NbtCompound)data.get("tag"));
            firedEntity.readNbt(mergedTag);
        }

        entity.world.spawnEntity(firedEntity);
    }

    public static ActionFactory<Entity> getFactory() {
        return new ActionFactory<>(Apugli.identifier("fire_projectile"),
                new SerializableData()
                    .add("count", SerializableDataTypes.INT, 1)
                    .add("speed", SerializableDataTypes.FLOAT, 1.5F)
                    .add("divergence", SerializableDataTypes.FLOAT, 1F)
                    .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                    .add("entity_type", SerializableDataTypes.ENTITY_TYPE)
                    .add("tag", SerializableDataTypes.NBT, null),
                FireProjectileAction::action
        );
    }
}