package net.merchantpug.apugli.entity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.ProjectileEntityAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.ApugliEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

public class CustomProjectile extends ThrowableProjectile {
    private static final EntityDataAccessor<String> DATA_ENTITY_ID = SynchedEntityData.defineId(CustomProjectile.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_URL_LOCATION = SynchedEntityData.defineId(CustomProjectile.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_TEXTURE_LOCATION = SynchedEntityData.defineId(CustomProjectile.class, EntityDataSerializers.STRING);
    private Object impactBlockAction;
    private Object missBiEntityAction;
    private Object impactBiEntityAction;
    private Object ownerImpactBiEntityAction;
    private boolean blockActionCancelsMissAction;
    private Object blockCondition;
    private Object biEntityCondition;
    private Object ownerBiEntityCondition;
    private Object tickBiEntityAction;

    public CustomProjectile(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public CustomProjectile(double d, double e, double f, LivingEntity owner, Level level) {
        super(ApugliEntityTypes.CUSTOM_PROJECTILE.get(), d, e, f, level);
        this.setOwner(owner);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!target.isSpectator() && target.isAlive() && target.isPickable()) {
            Entity entity = this.getOwner();
            return (entity == null || ((ProjectileEntityAccessor)this).getLeftOwner() || !entity.isPassengerOfSameVehicle(target)) && entity != null && Services.CONDITION.checkBiEntity(biEntityCondition, this, target) && Services.CONDITION.checkBiEntity(ownerBiEntityCondition, entity, target);
        } else {
            return false;
        }
    }

    @Override
    public Entity getOwner() {
        ProjectileEntityAccessor accessor = (ProjectileEntityAccessor)this;
        if (accessor.getCachedOwner() != null && !isOwnerRemovedOrDiscarded()) {
            return accessor.getCachedOwner();
        } else if (accessor.getOwnerUUID() != null && this.level() instanceof ServerLevel serverLevel) {
            accessor.setCachedOwner(serverLevel.getEntity(accessor.getOwnerUUID()));
            return accessor.getCachedOwner();
        } else {
            return null;
        }
    }

    private boolean isOwnerRemovedOrDiscarded() {
        ProjectileEntityAccessor accessor = (ProjectileEntityAccessor)this;
        if (accessor.getCachedOwner() == null) {
            return false;
        }
        return accessor.getCachedOwner().getRemovalReason() == RemovalReason.KILLED || accessor.getCachedOwner().getRemovalReason() == RemovalReason.DISCARDED;
    }

    @Override
    public void onHitEntity(EntityHitResult result) {
        Services.ACTION.executeBiEntity(impactBiEntityAction, this, result.getEntity());
        Services.ACTION.executeBiEntity(ownerImpactBiEntityAction, this.getOwner(), result.getEntity());
        this.discard();
    }

    @Override
    public void onHitBlock(BlockHitResult result) {
        boolean executedBlockAction = false;
        if (impactBlockAction != null && Services.CONDITION.checkBlock(blockCondition, this.level(), result.getBlockPos())) {
            Services.ACTION.executeBlock(impactBlockAction, this.level(), result.getBlockPos(), result.getDirection());
            executedBlockAction = true;
        }
        if (!executedBlockAction || !blockActionCancelsMissAction) {
            Services.ACTION.executeBiEntity(missBiEntityAction, this.getOwner(), this);
        }
        BlockState blockState = this.level().getBlockState(result.getBlockPos());
        blockState.onProjectileHit(this.level(), blockState, result, this);
        this.discard();
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ENTITY_ID, "");
        this.getEntityData().define(DATA_URL_LOCATION, "");
        this.getEntityData().define(DATA_TEXTURE_LOCATION, "");
    }

    @Override
    public void tick() {
        super.tick();
        if (isOwnerRemovedOrDiscarded()) {
            this.discard();
            return;
        }
        Services.ACTION.executeBiEntity(tickBiEntityAction, this.getOwner(), this);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (!this.getEntityData().get(DATA_ENTITY_ID).equals("")) {
            compound.putString("EntityId", this.getEntityData().get(DATA_ENTITY_ID));
        }
        if (!this.getEntityData().get(DATA_TEXTURE_LOCATION).equals("")) {
            compound.putString("TextureLocation", this.getEntityData().get(DATA_TEXTURE_LOCATION));
        }
        if (!this.getEntityData().get(DATA_URL_LOCATION).equals("")) {
            compound.putString("UrlLocation", this.getEntityData().get(DATA_URL_LOCATION));
        }
        Services.ACTION.writeBlockActionToNbt(compound, "ImpactBlockAction", impactBlockAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "MissBiEntityAction", missBiEntityAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "ImpactBiEntityAction", impactBiEntityAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "OwnerImpactBiEntityAction", ownerImpactBiEntityAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "TickBiEntityAction", tickBiEntityAction);
        Services.CONDITION.writeBlockConditionToNbt(compound, "BlockCondition", blockCondition);
        Services.CONDITION.writeBiEntityConditionToNbt(compound, "BiEntityCondition", biEntityCondition);
        Services.CONDITION.writeBiEntityConditionToNbt(compound, "OwnerBiEntityCondition", ownerBiEntityCondition);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("EntityId", Tag.TAG_STRING)) {
            this.getEntityData().set(DATA_ENTITY_ID, compound.getString("EntityId"));
        }
        if (compound.contains("TextureLocation", Tag.TAG_STRING)) {
            this.getEntityData().set(DATA_TEXTURE_LOCATION, compound.getString("TextureLocation"));
        }
        if (compound.contains("UrlLocation", Tag.TAG_STRING)) {
            this.getEntityData().set(DATA_URL_LOCATION, compound.getString("UrlLocation"));
        }
        impactBlockAction = Services.ACTION.readBlockActionFromNbt(compound, "ImpactBlockAction");
        missBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "MissBiEntityAction");
        impactBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "ImpactBiEntityAction");
        ownerImpactBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "OwnerImpactBiEntityAction");
        tickBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "TickBiEntityAction");
        blockCondition = Services.CONDITION.readBlockConditionFromNbt(compound, "BlockCondition");
        biEntityCondition = Services.CONDITION.readBiEntityConditionFromNbt(compound, "BiEntityCondition");
        ownerBiEntityCondition = Services.CONDITION.readBiEntityConditionFromNbt(compound, "OwnerBiEntityCondition");
    }

    public ResourceLocation getEntityId() {
        return new ResourceLocation(this.getEntityData().get(DATA_ENTITY_ID));
    }

    public ResourceLocation getUrlLocation() {
        if (this.getEntityData().get(DATA_URL_LOCATION).equals("")) {
            return null;
        }
        return new ResourceLocation(this.getEntityData().get(DATA_URL_LOCATION));
    }

    public ResourceLocation getTextureLocation() {
        if (this.getEntityData().get(DATA_TEXTURE_LOCATION).equals("")) {
            return null;
        }
        return new ResourceLocation(this.getEntityData().get(DATA_TEXTURE_LOCATION));
    }

    public void setEntityId(ResourceLocation location) {
        this.getEntityData().set(DATA_ENTITY_ID, location.toString());
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

    public void setBlockCondition(SerializableData.Instance data, String fieldName) {
        blockCondition = data.get(fieldName);
    }

    public void setBiEntityCondition(SerializableData.Instance data, String fieldName) {
        biEntityCondition = data.get(fieldName);
    }

    public void setOwnerBiEntityCondition(SerializableData.Instance data, String fieldName) {
        ownerBiEntityCondition = data.get(fieldName);
    }

    public void setTickBiEntityAction(SerializableData.Instance data, String fieldName) {
        tickBiEntityAction = data.get(fieldName);
    }

}
