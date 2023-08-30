package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricActiveCooldownConfiguration;
import net.merchantpug.apugli.power.factory.CustomProjectilePowerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@AutoService(CustomProjectilePowerFactory.class)
public class CustomProjectilePower extends AbstractActiveCooldownPower implements CustomProjectilePowerFactory<ConfiguredPower<FabricActiveCooldownConfiguration, ?>> {

    public CustomProjectilePower() {
        super(CustomProjectilePowerFactory.getSerializableData().xmap(
                FabricActiveCooldownConfiguration::new,
                FabricActiveCooldownConfiguration::data
        ).codec());
        this.ticking();
    }

    protected DataContainer access(ConfiguredPower<FabricActiveCooldownConfiguration, ?> configuration, IPowerContainer container) {
        return configuration.getPowerData(container, DataContainer::new);
    }

    @Override
    public void execute(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        this.setFiringProjectiles(power, entity, true);
    }

    @Override
    public void tick(ConfiguredPower<FabricActiveCooldownConfiguration, ?> configuration, Entity entity) {
        if (entity instanceof LivingEntity living) {
            CustomProjectilePowerFactory.super.tick(configuration, living);
        }
    }

    @Override
    public void serialize(ConfiguredPower<FabricActiveCooldownConfiguration, ?> configuration, IPowerContainer container, CompoundTag tag) {
        this.access(configuration, container).serialize(tag);
    }

    @Override
    public void deserialize(ConfiguredPower<FabricActiveCooldownConfiguration, ?> configuration, IPowerContainer container, CompoundTag tag) {
        this.access(configuration, container).deserialize(tag);
    }

    @Override
    public ResourceLocation getPowerId(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power) {
        return power.getRegistryName();
    }

    @Override
    protected long getLastUseTime(ConfiguredPower<FabricActiveCooldownConfiguration, ?> configuration, @Nullable IPowerContainer container) {
        return container != null ? this.access(configuration, container).lastUseTime : Long.MAX_VALUE;
    }

    @Override
    protected void setLastUseTime(ConfiguredPower<FabricActiveCooldownConfiguration, ?> configuration, @Nullable IPowerContainer container, long value) {
        if (container != null) {
            this.access(configuration, container).lastUseTime = value;
        }
    }

    @Override
    public int getShotProjectiles(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        return this.access(power, ApoliAPI.getPowerContainer(entity)).shotProjectiles;
    }

    @Override
    public boolean isFiringProjectiles(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        return this.access(power, ApoliAPI.getPowerContainer(entity)).isFiringProjectiles;
    }

    @Override
    public boolean finishedStartDelay(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        return this.access(power, ApoliAPI.getPowerContainer(entity)).finishedStartDelay;
    }

    @Override
    public void setShotProjectiles(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity, int value) {
        this.access(power, ApoliAPI.getPowerContainer(entity)).shotProjectiles = value;
    }

    @Override
    public void setFiringProjectiles(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity, boolean value) {
        this.access(power, ApoliAPI.getPowerContainer(entity)).isFiringProjectiles = value;
    }

    @Override
    public void setFinishedStartDelay(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity, boolean value) {
        this.access(power, ApoliAPI.getPowerContainer(entity)).finishedStartDelay = value;
    }

    @Override
    public long getLastUseTime(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        return super.getLastUseTime(power, entity);
    }

    private static class DataContainer {
        private long lastUseTime = Long.MIN_VALUE;
        private int shotProjectiles;
        private boolean finishedStartDelay;
        private boolean isFiringProjectiles;

        private DataContainer() {
        }

        public void serialize(CompoundTag tag) {
            tag.putLong("LastUseTime", this.lastUseTime);
            tag.putInt("ShotProjectiles", this.shotProjectiles);
            tag.putBoolean("FinishedStartDelay", this.finishedStartDelay);
            tag.putBoolean("IsFiringProjectiles", this.isFiringProjectiles);
        }

        public void deserialize(CompoundTag tag) {
            this.lastUseTime = tag.getLong("LastUseTime");
            this.shotProjectiles = tag.getInt("ShotProjectiles");
            this.finishedStartDelay = tag.getBoolean("FinishedStartDelay");
            this.isFiringProjectiles = tag.getBoolean("IsFiringProjectiles");
        }
    }
}
