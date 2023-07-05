package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.mixin.fabric.common.accessor.CooldownPowerAccessor;
import net.merchantpug.apugli.power.factory.CustomProjectilePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@AutoService(CustomProjectilePowerFactory.class)
public class CustomProjectilePower extends AbstractActiveCooldownPower<CustomProjectilePower.Instance> implements CustomProjectilePowerFactory<CustomProjectilePower.Instance> {

    public CustomProjectilePower() {
        super("custom_projectile", CustomProjectilePowerFactory.getSerializableData(),
                data -> (type, entity) -> new CustomProjectilePower.Instance(type, entity, data));
        allowCondition();
    }

    @Override
    public SerializableData.Instance getDataFromPower(Instance power) {
        return power.data;
    }

    @Override
    public Class<CustomProjectilePower.Instance> getPowerClass() {
        return CustomProjectilePower.Instance.class;
    }

    @Override
    public ResourceLocation getPowerId(Instance power) {
        return power.getType().getIdentifier();
    }

    @Override
    public long getLastUseTime(Instance power, Entity entity) {
        return ((CooldownPowerAccessor)power).getLastUseTime();
    }

    @Override
    public int getShotProjectiles(Instance power, Entity entity) {
        return power.shotProjectiles;
    }

    @Override
    public boolean isFiringProjectiles(Instance power, Entity entity) {
        return power.isFiringProjectiles;
    }

    @Override
    public boolean finishedStartDelay(Instance power, Entity entity) {
        return power.finishedStartDelay;
    }

    @Override
    public void setShotProjectiles(Instance power, Entity entity, int value) {
        power.shotProjectiles = value;
    }

    @Override
    public void setFiringProjectiles(Instance power, Entity entity, boolean value) {
        power.isFiringProjectiles = value;
    }

    @Override
    public void setFinishedStartDelay(Instance power, Entity entity, boolean value) {
        power.finishedStartDelay = value;
    }

    public static class Instance extends AbstractActiveCooldownPower.Instance {
        private int shotProjectiles;
        private boolean finishedStartDelay;
        private boolean isFiringProjectiles;

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
            this.setTicking();
        }

        @Override
        public void tick() {
            ApugliPowers.CUSTOM_PROJECTILE.get().tick(this, entity);
        }

        @Override
        public void onUse() {
            if (canUse()) {
                isFiringProjectiles = true;
                this.use();
            }
        }

        public Tag toTag() {
            CompoundTag nbt = new CompoundTag();
            nbt.putLong("LastUseTime", this.lastUseTime);
            nbt.putInt("ShotProjectiles", this.shotProjectiles);
            nbt.putBoolean("FinishedStartDelay", this.finishedStartDelay);
            nbt.putBoolean("IsFiringProjectiles", this.isFiringProjectiles);
            return nbt;
        }

        public void fromTag(Tag tag) {
            if (tag instanceof CompoundTag compound) {
                this.lastUseTime = compound.getLong("LastUseTime");
                this.shotProjectiles = compound.getInt("ShotProjectiles");
                this.finishedStartDelay = compound.getBoolean("FinishedStartDelay");
                this.isFiringProjectiles = compound.getBoolean("IsFiringProjectiles");
            }
        }

    }

}
