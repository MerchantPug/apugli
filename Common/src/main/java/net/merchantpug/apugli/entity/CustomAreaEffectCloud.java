package net.merchantpug.apugli.entity;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.AreaEffectCloudEntityAccessor;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.registry.ApugliEntityTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.*;

public class CustomAreaEffectCloud extends AreaEffectCloud {
    private static final EntityDataAccessor<String> DATA_ENTITY_ID = SynchedEntityData.defineId(CustomAreaEffectCloud.class, EntityDataSerializers.STRING);

    private final List<ResourceLocation> powersToApply = new ArrayList<>();
    private Object ownerCloudBiEntityAction;
    private Object ownerTargetBiEntityAction;
    private Object cloudTargetBiEntityAction;
    private Object biEntityCondition;
    private Object ownerTargetBiEntityCondition;
    private double heightIncrease = 0.0;
    private final Map<LivingEntity, Integer> victims = new HashMap<>();

    public CustomAreaEffectCloud(EntityType<CustomAreaEffectCloud> entityType, Level world) {
        super(entityType, world);
        this.noPhysics = true;
    }

    public CustomAreaEffectCloud(Level world, double x, double y, double z) {
        this(ApugliEntityTypes.CUSTOM_AREA_EFFECT_CLOUD.get(), world);
        this.setPos(x, y, z);
    }

    @Override
    public void remove(RemovalReason reason) {
        for (Map.Entry<LivingEntity, Integer> next : this.victims.entrySet()) {
            for (ResourceLocation power : this.powersToApply) {
                Services.POWER.revokePower(power, getEntityId(), next.getKey());
            }
        }
        super.remove(reason);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_ENTITY_ID, "");
    }

    public void setHeightIncrease(double value) {
        this.heightIncrease = value;
    }

    public void setOwnerCloudBiEntityAction(SerializableData.Instance data, String fieldName) {
        ownerCloudBiEntityAction = data.get(fieldName);
    }

    public void setOwnerTargetBiEntityAction(SerializableData.Instance data, String fieldName) {
        ownerTargetBiEntityAction = data.get(fieldName);
    }

    public void setCloudTargetBiEntityAction(SerializableData.Instance data, String fieldName) {
        cloudTargetBiEntityAction = data.get(fieldName);
    }

    public void setBiEntityCondition(SerializableData.Instance data, String fieldName) {
        biEntityCondition = data.get(fieldName);
    }

    public void setOwnerTargetBiEntityCondition(SerializableData.Instance data, String fieldName) {
        ownerTargetBiEntityCondition = data.get(fieldName);
    }

    public <P> void addPowerToApply(P value) {
        ResourceLocation location = Services.POWER.getPowerFromParameter(value);
        if (location == null) return;
        powersToApply.add(location);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if (!this.getEntityData().get(DATA_ENTITY_ID).equals("")) {
            compound.putString("EntityId", this.getEntityData().get(DATA_ENTITY_ID));
        }

        ListTag powerTag = new ListTag();
        for (ResourceLocation power : powersToApply) {
            powerTag.add(StringTag.valueOf(power.toString()));
        }
        if (!powerTag.isEmpty())
            compound.put("PowersToApply", powerTag);
        compound.putDouble("HeightIncrease", heightIncrease);
        Services.ACTION.writeBiEntityActionToNbt(compound, "OwnerCloudBiEntityAction", ownerCloudBiEntityAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "OwnerTargetBiEntityAction", ownerTargetBiEntityAction);
        Services.ACTION.writeBiEntityActionToNbt(compound, "CloudTargetBiEntityAction", cloudTargetBiEntityAction);
        Services.CONDITION.writeBiEntityConditionToNbt(compound, "BiEntityCondition", biEntityCondition);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("EntityId", Tag.TAG_STRING)) {
            this.getEntityData().set(DATA_ENTITY_ID, compound.getString("EntityId"));
        }
        if (compound.contains("PowersToApply", Tag.TAG_LIST)) {
            ListTag powerTag = compound.getList("PowersToApply", Tag.TAG_STRING);

            for (Tag tag : powerTag) {
                powersToApply.add(new ResourceLocation(tag.getAsString()));
            }
        }
        heightIncrease = compound.getDouble("HeightIncrease");
        ownerCloudBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "OwnerCloudBiEntityAction");
        ownerTargetBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "OwnerTargetBiEntityAction");
        cloudTargetBiEntityAction = Services.ACTION.readBiEntityActionFromNbt(compound, "CloudTargetBiEntityAction");
        biEntityCondition = Services.CONDITION.readBiEntityConditionFromNbt(compound, "BiEntityCondition");
    }

    public ResourceLocation getEntityId() {
        return new ResourceLocation(this.getEntityData().get(DATA_ENTITY_ID));
    }

    public void setEntityId(ResourceLocation location) {
        this.getEntityData().set(DATA_ENTITY_ID, location.toString());
    }

    public void tick() {
        super.tick();
        boolean bl = this.isWaiting();
        float f = this.getRadius();
        if (this.level().isClientSide) {
            if (bl && this.random.nextBoolean()) {
                return;
            }

            ParticleOptions particleOptions = this.getParticle();
            int i;
            float g;
            if (bl) {
                i = 2;
                g = 0.2F;
            } else {
                i = Mth.ceil(3.1415927F * f * f);
                g = f;
            }

            for(int j = 0; j < i; ++j) {
                float h = this.random.nextFloat() * 6.2831855F;
                float k = Mth.sqrt(this.random.nextFloat()) * g;
                double d = this.getX() + (double)(Mth.cos(h) * k);
                double e = this.getY();
                double l = this.getZ() + (double)(Mth.sin(h) * k);
                double n;
                double o;
                double p;
                if (particleOptions.getType() != ParticleTypes.ENTITY_EFFECT) {
                    if (bl) {
                        n = 0.0;
                        o = 0.0;
                        p = 0.0;
                    } else {
                        n = (0.5 - this.random.nextDouble()) * 0.15;
                        o = 0.009999999776482582;
                        p = (0.5 - this.random.nextDouble()) * 0.15;
                    }
                } else {
                    int m = bl && this.random.nextBoolean() ? 16777215 : this.getColor();
                    n = (float)(m >> 16 & 255) / 255.0F;
                    o = (float)(m >> 8 & 255) / 255.0F;
                    p = (float)(m & 255) / 255.0F;
                }

                this.level().addAlwaysVisibleParticle(particleOptions, d, e, l, n, o, p);
            }
        } else {
            if (this.tickCount >= this.getWaitTime() + this.getDuration()) {
                this.discard();
                return;
            }

            boolean bl2 = this.tickCount < this.getWaitTime();
            if (bl != bl2) {
                this.setWaiting(bl2);
            }

            if (bl2) {
                return;
            }

            if (this.getRadiusPerTick() != 0.0F) {
                f += this.getRadiusPerTick();
                if (f < 0.5F) {
                    this.discard();
                    return;
                }

                this.setRadius(f);
            }

            if (this.tickCount % 5 == 0) {
                List<LivingEntity> list2 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(0.0, heightIncrease, 0.0));

                Iterator<Map.Entry<LivingEntity, Integer>> victimIterator = this.victims.entrySet().iterator();

                while (victimIterator.hasNext()) {
                    var next = victimIterator.next();
                    if (this.tickCount >= next.getValue()) {
                        if (!list2.contains(next.getKey())) {
                            for (ResourceLocation power : powersToApply) {
                                Services.POWER.revokePower(power, getEntityId(), next.getKey());
                            }
                            victimIterator.remove();
                        }
                    }
                }

                if (!list2.isEmpty() && !this.isRemoved()) {
                    Iterator<LivingEntity> var27 = list2.iterator();

                    while (true) {
                        double s;
                        LivingEntity livingEntity;
                        do {
                            do {
                                if (!var27.hasNext()) {
                                    return;
                                }

                                livingEntity = var27.next();
                            } while (!Services.CONDITION.checkBiEntity(biEntityCondition, this, livingEntity) || !Services.CONDITION.checkBiEntity(ownerTargetBiEntityCondition, this.getOwner(), livingEntity));

                            double q = livingEntity.getX() - this.getX();
                            double r = livingEntity.getZ() - this.getZ();
                            s = q * q + r * r;
                        } while (!(s <= (double) (f * f)));

                        this.victims.put(livingEntity, this.tickCount + ((AreaEffectCloudEntityAccessor) this).getReapplicationDelay());

                        for (ResourceLocation power : powersToApply) {
                            if (!Services.POWER.hasPowerType(power, getEntityId(), livingEntity)) {
                                Services.POWER.grantPower(power, getEntityId(), livingEntity);
                            }
                        }

                        Services.ACTION.executeBiEntity(ownerCloudBiEntityAction, this.getOwner(), this);
                        Services.ACTION.executeBiEntity(ownerTargetBiEntityAction, this.getOwner(), livingEntity);
                        Services.ACTION.executeBiEntity(cloudTargetBiEntityAction, this, livingEntity);

                        if (this.getRadiusOnUse() != 0.0F) {
                            f += this.getRadiusOnUse();
                            if (f < 0.5F) {
                                this.discard();
                                return;
                            }

                            this.setRadius(f);
                        }

                        if (this.getDurationOnUse() != 0) {
                            this.setDuration(this.getDuration() + this.getDurationOnUse());
                            if (this.getDuration() <= 0) {
                                this.discard();
                                return;
                            }
                        }
                    }
                }
            }
        }

    }

}
