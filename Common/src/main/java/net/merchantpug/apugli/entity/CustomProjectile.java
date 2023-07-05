package net.merchantpug.apugli.entity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.ApugliEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

public class CustomProjectile extends ThrowableProjectile {
    private static final EntityDataAccessor<String> DATA_URL_LOCATION = SynchedEntityData.defineId(CustomProjectile.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_TEXTURE_LOCATION = SynchedEntityData.defineId(CustomProjectile.class, EntityDataSerializers.STRING);
    private Object impactBlockAction;
    private Object missBiEntityAction;
    private Object impactBiEntityAction;
    private Object ownerImpactBiEntityAction;
    private boolean blockActionCancelsMissAction;

    public CustomProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public CustomProjectile(double d, double e, double f, LivingEntity owner, Level level) {
        super(ApugliEntityTypes.CUSTOM_PROJECTILE.get(), d, e, f, level);
        this.setOwner(owner);
    }

    @Override
    public void onHitEntity(EntityHitResult result) {
        Services.ACTION.executeBiEntity(impactBiEntityAction, this, result.getEntity());
        Services.ACTION.executeBiEntity(ownerImpactBiEntityAction, this.getOwner(), result.getEntity());
        this.discard();
    }

    @Override
    public void onHitBlock(BlockHitResult result) {
        Services.ACTION.executeBlock(impactBlockAction, this.getLevel(), result.getBlockPos(), result.getDirection());
        if (!blockActionCancelsMissAction) {
            Services.ACTION.executeBiEntity(missBiEntityAction, this.getOwner(), this);
        }
        this.discard();
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_URL_LOCATION, "");
        this.getEntityData().define(DATA_TEXTURE_LOCATION, "");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Services.ACTION.writeBlockActionToNbt(compound, "ImpactBlockAction", impactBlockAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "MissBiEntityAction", missBiEntityAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "ImpactBiEntityAction", impactBiEntityAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "OwnerImpactBiEntityAction", ownerImpactBiEntityAction);
        if (!this.getEntityData().get(DATA_TEXTURE_LOCATION).equals("")) {
            compound.putString("TextureLocation", this.getEntityData().get(DATA_TEXTURE_LOCATION));
        }
        if (!this.getEntityData().get(DATA_URL_LOCATION).equals("")) {
            compound.putString("UrlLocation", this.getEntityData().get(DATA_URL_LOCATION));
        }
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        impactBlockAction = Services.ACTION.readBlockActionFromNbt(compound, "ImpactBlockAction");
        missBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "MissBiEntityAction");
        impactBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "ImpactBiEntityAction");
        ownerImpactBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "OwnerImpactBiEntityAction");
        if (compound.contains("TextureLocation", Tag.TAG_STRING)) {
            this.getEntityData().set(DATA_TEXTURE_LOCATION, compound.getString("TextureLocation"));
        }
        if (compound.contains("UrlLocation", Tag.TAG_STRING)) {
            this.getEntityData().set(DATA_URL_LOCATION, compound.getString("UrlLocation"));
        }
    }

    public ResourceLocation getUrlLocation() {
        if (this.getEntityData().get(DATA_URL_LOCATION).equals("")) {
            return null;
        }
        return ResourceLocation.of(this.getEntityData().get(DATA_URL_LOCATION), ':');
    }

    public ResourceLocation getTextureLocation() {
        if (this.getEntityData().get(DATA_TEXTURE_LOCATION).equals("")) {
            return null;
        }
        return ResourceLocation.of(this.getEntityData().get(DATA_TEXTURE_LOCATION), ':');
    }

    public void setUrlLocation(ResourceLocation location) {
        this.getEntityData().set(DATA_URL_LOCATION, location.toString());
    }

    public void setTextureLocation(@Nullable ResourceLocation location) {
        if (location != null) {
            this.getEntityData().set(DATA_TEXTURE_LOCATION, location.toString());
        }
    }

    public void setBlockActionCancelsMissAction(boolean value) {
        blockActionCancelsMissAction = value;
    }

    public void setImpactBlockAction(SerializableData.Instance data, String fieldName) {
        impactBlockAction = data.get(fieldName);
    }

    public void setMissBiEntityAction(SerializableData.Instance data, String fieldName) {
        missBiEntityAction = data.get(fieldName);
    }

    public void setImpactBiEntityAction(SerializableData.Instance data, String fieldName) {
        impactBiEntityAction = data.get(fieldName);
    }

    public void setOwnerImpactBiEntityAction(SerializableData.Instance data, String fieldName) {
        ownerImpactBiEntityAction = data.get(fieldName);
    }

}
