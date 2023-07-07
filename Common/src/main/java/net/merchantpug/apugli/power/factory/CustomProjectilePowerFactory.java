package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.entity.CustomProjectile;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.util.TextureUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Locale;

public interface CustomProjectilePowerFactory<P> extends ActiveCooldownPowerFactory<P> {

    static SerializableData getSerializableData() {
        return ActiveCooldownPowerFactory.getSerializableData()
                .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                .add("texture_url", SerializableDataTypes.STRING, null)
                .add("count", SerializableDataTypes.INT, 1)
                .add("interval", SerializableDataTypes.INT, 0)
                .add("start_delay", SerializableDataTypes.INT, 0)
                .add("speed", SerializableDataTypes.FLOAT, 1.5F)
                .add("divergence", SerializableDataTypes.FLOAT, 1F)
                .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                .add("tag", SerializableDataTypes.NBT, null)
                .add("entity_action_before_firing", Services.ACTION.entityDataType(), null)
                .add("bientity_action_after_firing", Services.ACTION.biEntityDataType(), null)
                .add("block_action_on_hit", Services.ACTION.blockDataType(), null)
                .add("bientity_action_on_miss", Services.ACTION.biEntityDataType(), null)
                .add("bientity_action_on_hit", Services.ACTION.biEntityDataType(), null)
                .add("owner_target_bientity_action_on_hit", Services.ACTION.biEntityDataType(), null)
                .add("allow_conditional_cancelling", SerializableDataTypes.BOOLEAN, false)
                .add("block_action_cancels_miss_action", SerializableDataTypes.BOOLEAN, false)
                .add("block_condition", Services.CONDITION.blockDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("owner_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("tick_bientity_action", Services.ACTION.biEntityDataType(), null);
    }

    default void cacheTextureUrl(ResourceLocation powerId, P power) {
        ResourceLocation urlTextureLocation = getUrlTextureIdentifier(powerId);
        TextureUtil.getCache().add(Triple.of(urlTextureLocation, getDataFromPower(power).getString("texture_url"), getDataFromPower(power).getId("texture_location")));
    }

    default ResourceLocation getUrlTextureIdentifier(ResourceLocation powerId) {
        return new ResourceLocation(Apugli.ID, getPowerClassString().toLowerCase(Locale.ROOT) + "/" + powerId.getNamespace() + "/" + powerId.getPath());
    }

    default String getPowerClassString() {
        return "CustomProjectilePower";
    }


    ResourceLocation getPowerId(P power);

    long getLastUseTime(P power, Entity entity);
    int getShotProjectiles(P power, Entity entity);
    boolean isFiringProjectiles(P power, Entity entity);
    boolean finishedStartDelay(P power, Entity entity);

    void setShotProjectiles(P power, Entity entity, int value);
    void setFiringProjectiles(P power, Entity entity, boolean value);
    void setFinishedStartDelay(P power, Entity entity, boolean value);

    default void tick(P power, LivingEntity entity) {
        if (this.isFiringProjectiles(power, entity)) {
            SerializableData.Instance data = getDataFromPower(power);
            if (!finishedStartDelay(power, entity) && data.getInt("start_delay") == 0) {
                setFinishedStartDelay(power, entity, true);
            }

            if (!finishedStartDelay(power, entity) && (entity.getCommandSenderWorld().getGameTime() - this.getLastUseTime(power, entity)) % (long)data.getInt("start_delay") == 0L) {
                setFinishedStartDelay(power, entity, true);
                setShotProjectiles(power, entity, getShotProjectiles(power, entity) + 1);
                if (this.getShotProjectiles(power, entity) <= data.getInt("count")) {
                    playSound(data, entity);

                    if (!entity.level.isClientSide) {
                        this.fireProjectile(power, data, entity);
                    }
                } else {
                    reset(power, entity);
                }
            } else if (data.getInt("interval") == 0 && finishedStartDelay(power, entity)) {
                playSound(data, entity);

                if (!entity.level.isClientSide) {
                    while(this.getShotProjectiles(power, entity) < data.getInt("count")) {
                        this.fireProjectile(power, data, entity);
                        setShotProjectiles(power, entity, getShotProjectiles(power, entity) + 1);
                    }
                }

                reset(power, entity);
            } else if (finishedStartDelay(power, entity) && (entity.getCommandSenderWorld().getGameTime() - getLastUseTime(power, entity)) % (long)data.getInt("interval") == 0L) {
                setShotProjectiles(power, entity, getShotProjectiles(power, entity) + 1);
                if (this.getShotProjectiles(power, entity) <= data.getInt("count")) {
                    playSound(data, entity);

                    if (!entity.level.isClientSide) {
                        this.fireProjectile(power, data, entity);
                    }
                } else {
                    reset(power, entity);
                }
            }
        }
    }

    default void playSound(SerializableData.Instance data, LivingEntity entity) {
        if (data.get("sound") != null) {
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), data.get("sound"), SoundSource.NEUTRAL, 0.5F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    default void reset(P power, LivingEntity entity) {
        setShotProjectiles(power, entity, 0);
        setFinishedStartDelay(power, entity, false);
        setFiringProjectiles(power, entity, false);
    }

    default void fireProjectile(P power, SerializableData.Instance data, LivingEntity entity) {
        Services.ACTION.executeEntity(data, "entity_action_before_firing", entity);

        if (data.getBoolean("allow_conditional_cancelling") && !Services.POWER.isActive(power, entity)) {
            setFiringProjectiles(power, entity, false);
            return;
        }

        float yaw = entity.getYRot();
        float pitch = entity.getXRot();
        Vec3 rotationVec = entity.getLookAngle();
        Vec3 spawnPos = entity.position().add(0.0D, entity.getEyeHeight(), 0.0D).add(rotationVec);
        CustomProjectile projectile = new CustomProjectile(spawnPos.x(), spawnPos.y(), spawnPos.z(), entity, entity.level);

        projectile.setEntityId(getPowerId(power));
        projectile.setOwner(entity);
        projectile.shootFromRotation(entity, pitch, yaw, 0.0F, data.getFloat("speed"), data.getFloat("divergence") * 0.075F);
        projectile.setImpactBlockAction(data, "block_action_on_hit");
        projectile.setBlockActionCancelsMissAction(data.getBoolean("block_action_cancels_miss_action"));
        projectile.setMissBiEntityAction(data, "bientity_action_on_miss");
        projectile.setImpactBiEntityAction(data, "bientity_action_on_hit");
        projectile.setOwnerImpactBiEntityAction(data, "owner_target_bientity_action_on_hit");
        projectile.setTextureLocation(data.getId("texture_location"));
        projectile.setUrlLocation(getUrlTextureIdentifier(getPowerId(power)));
        projectile.setBlockCondition(data, "block_condition");
        projectile.setOwnerBiEntityCondition(data, "owner_bientity_condition");
        projectile.setBiEntityCondition(data, "bientity_condition");
        projectile.setTickBiEntityAction(data, "tick_bientity_action");

        if (data.get("tag") != null) {
            CompoundTag mergedTag = entity.saveWithoutId(new CompoundTag());
            mergedTag.merge(data.get("tag"));
            entity.load(mergedTag);
        }

        entity.level.addFreshEntity(projectile);
        Services.ACTION.executeBiEntity(data, "bientity_action_after_firing", entity, projectile);
    }

}
