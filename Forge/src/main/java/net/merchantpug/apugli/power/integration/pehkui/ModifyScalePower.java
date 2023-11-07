package net.merchantpug.apugli.power.integration.pehkui;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.common.power.FireProjectilePower;
import io.github.edwinmindcraft.apoli.common.power.configuration.FireProjectileConfiguration;
import net.merchantpug.apugli.access.ScaleDataAccess;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.power.AbstractValueModifyingPower;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyScalePowerFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AutoService(ModifyScalePowerFactory.class)
public class ModifyScalePower extends AbstractValueModifyingPower implements ModifyScalePowerFactory<ConfiguredPower<FabricValueModifyingConfiguration, ?>> {

    public ModifyScalePower() {
        super(ModifyScalePowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
        this.ticking(true);
    }

    protected PowerData access(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuration, IPowerContainer container) {
        if (!ModList.get().isLoaded("pehkui")) {
            return null;
        }
        return configuration.getPowerData(container, () -> new PowerData(configuration, container.getOwner()));
    }

    @Override
    public void tick(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            PehkuiUtil.tickScalePower(power, living);
    }

    @Override
    public void onRemoved(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            PehkuiUtil.onRemovedScalePower(power, living);
    }

    @Override
    public void serialize(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuration, IPowerContainer container, CompoundTag tag) {
        if (container.getOwner() instanceof LivingEntity living)
            PehkuiUtil.serializeScalePower(configuration, living, tag);
    }

    @Override
    public void deserialize(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuration, IPowerContainer container, CompoundTag tag) {
        if (container.getOwner() instanceof LivingEntity living)
            PehkuiUtil.deserializeScalePower(configuration, living, tag);
    }

    @Override
    public ResourceLocation getPowerId(ConfiguredPower<FabricValueModifyingConfiguration, ?> power) {
        return power.getRegistryName();
    }

    @Override
    public Object getApoliScaleModifier(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        return this.access(power, ApoliAPI.getPowerContainer(entity)).apoliScaleModifier;
    }

    @Override
    public Set<ResourceLocation> getCachedScaleIds(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        return this.access(power, ApoliAPI.getPowerContainer(entity)).cachedScaleIds;
    }

    public static class PowerData {
        private final Object apoliScaleModifier;
        private final Set<ResourceLocation> cachedScaleIds;

        public PowerData(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
            if (ModList.get().isLoaded("pehkui")) {
                this.apoliScaleModifier = PehkuiUtil.createApoliScaleModifier(power, entity);
                this.cachedScaleIds = PehkuiUtil.getTypesFromCache(power);
            } else {
                this.apoliScaleModifier = null;
                this.cachedScaleIds = Set.of();
            }
        }
    }

}
