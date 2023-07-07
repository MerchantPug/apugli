package net.merchantpug.apugli.action.factory.entity;

import com.google.common.hash.Hashing;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.entity.CustomProjectile;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;

public class CustomProjectileAction implements IActionFactory<Entity> {

    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
                .add("entity_id", SerializableDataTypes.IDENTIFIER)
                .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                .add("texture_url", SerializableDataTypes.STRING, null)
                .add("count", SerializableDataTypes.INT, 1)
                .add("speed", SerializableDataTypes.FLOAT, 1.5F)
                .add("divergence", SerializableDataTypes.FLOAT, 1F)
                .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                .add("tag", SerializableDataTypes.NBT, null)
                .add("bientity_action", Services.ACTION.biEntityDataType(), null)
                .add("entity_action_before_firing", Services.ACTION.entityDataType(), null)
                .add("bientity_action_after_firing", Services.ACTION.biEntityDataType(), null)
                .add("block_action_on_hit", Services.ACTION.blockDataType(), null)
                .add("bientity_action_on_miss", Services.ACTION.biEntityDataType(), null)
                .add("bientity_action_on_hit", Services.ACTION.biEntityDataType(), null)
                .add("owner_target_bientity_action_on_hit", Services.ACTION.biEntityDataType(), null)
                .add("block_action_cancels_miss_action", SerializableDataTypes.BOOLEAN, false)
                .add("block_condition", Services.CONDITION.blockDataType(), null)
                .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("owner_bientity_condition", Services.CONDITION.biEntityDataType(), null)
                .add("tick_bientity_action", Services.ACTION.biEntityDataType(), null);
    }

    @Override
    public void execute(SerializableData.Instance data, Entity entity) {
        if (data.isPresent("sound")) {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), data.get("sound"), SoundSource.NEUTRAL, 0.5F, 0.4F / (((LivingEntity) entity).getRandom().nextFloat() * 0.4F + 0.8F));
        }

        for (int i = 0; i < data.getInt("count"); ++i) {
            Entity result = createProjectile(data, (LivingEntity) entity);
            if (result != null) {
                entity.level().addFreshEntity(result);
                Services.ACTION.executeBiEntity(data, "bientity_action_after_firing", entity, result);
            }
        }
    }

    @Nullable
    protected Entity createProjectile(SerializableData.Instance data, LivingEntity actor) {
        Services.ACTION.executeEntity(data, "entity_action_before_firing", actor);

        float yaw = actor.getYRot();
        float pitch = actor.getXRot();
        Vec3 rotationVec = actor.getLookAngle();
        Vec3 spawnPos = actor.position().add(0.0D, actor.getEyeHeight(), 0.0D).add(rotationVec);
        CustomProjectile projectile = new CustomProjectile(spawnPos.x(), spawnPos.y(), spawnPos.z(), actor, actor.level());

        projectile.setEntityId(data.getId("entity_id"));
        projectile.setOwner(actor);
        projectile.shootFromRotation(actor, pitch, yaw, 0.0F, data.getFloat("speed"), data.getFloat("divergence") * 0.075F);
        projectile.setImpactBlockAction(data, "block_action_on_hit");
        projectile.setBlockActionCancelsMissAction(data.getBoolean("block_action_cancels_miss_action"));
        projectile.setMissBiEntityAction(data, "bientity_action_on_miss");
        projectile.setImpactBiEntityAction(data, "bientity_action_on_hit");
        projectile.setOwnerImpactBiEntityAction(data, "owner_target_bientity_action_on_hit");
        projectile.setTextureLocation(data.getId("texture_location"));
        projectile.setBlockCondition(data, "block_condition");
        projectile.setOwnerBiEntityCondition(data, "owner_bientity_condition");
        projectile.setBiEntityCondition(data, "bientity_condition");
        projectile.setTickBiEntityAction(data, "tick_bientity_action");
        if (data.isPresent("texture_url")) {
            projectile.setUrlLocation(getTextureUrl(data.getString("texture_url")));
        }

        if (data.get("tag") != null) {
            CompoundTag mergedTag = actor.saveWithoutId(new CompoundTag());
            mergedTag.merge(data.get("tag"));
            actor.load(mergedTag);
        }

        return projectile;
    }

    public static ResourceLocation getTextureUrl(String textureUrl) {
        int sha256Hash = Hashing.sha256().hashString(textureUrl, StandardCharsets.UTF_8).asInt();
        return new ResourceLocation(Apugli.ID, "customprojectileaction/" + sha256Hash);
    }

}