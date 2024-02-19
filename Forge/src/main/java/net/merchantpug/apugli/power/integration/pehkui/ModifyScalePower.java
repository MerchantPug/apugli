package net.merchantpug.apugli.power.integration.pehkui;

import com.google.auto.service.AutoService;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.integration.pehkui.PehkuiUtil;
import net.merchantpug.apugli.power.AbstractValueModifyingPower;
import net.merchantpug.apugli.power.configuration.FabricValueModifyingConfiguration;
import net.merchantpug.apugli.power.factory.ModifyScalePowerFactory;
import net.merchantpug.apugli.registry.power.ApugliPowers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
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
        return configuration.getPowerData(container, () -> new PowerData(configuration, container.getOwner()));
    }

    @Override
    public void onAdded(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            PehkuiUtil.onAddedOrRespawnedScalePower(power, living);
    }

    @Override
    public void onRespawn(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            PehkuiUtil.onAddedOrRespawnedScalePower(power, living);
    }

    @Override
    public void onRemoved(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living) {
            PehkuiUtil.onRemovedScalePower(power, living);
        }
    }

    @Override
    public void onLost(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        PehkuiUtil.resetScalePower(access(power, ApoliAPI.getPowerContainer(entity)).apoliScaleModifier);
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
    public List<?> getDelayModifiers(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        List<ConfiguredModifier<?>> modifiers = new ArrayList<>();
        this.getDataFromPower(power).<List<ConfiguredModifier<?>>>ifPresent("delay_modifiers", modifiers::addAll);
        this.getDataFromPower(power).<ConfiguredModifier<?>>ifPresent("delay_modifier", modifiers::add);
        return modifiers;
    }

    @Override
    public Set<ResourceLocation> getCachedScaleIds(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        return this.access(power, ApoliAPI.getPowerContainer(entity)).cachedScaleIds;
    }

    public static class PowerData {
        private final Object apoliScaleModifier;
        private final Set<ResourceLocation> cachedScaleIds;

        public PowerData(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
            if (ModList.get().isLoaded("pehkui") && entity instanceof LivingEntity living) {
                this.cachedScaleIds = PehkuiUtil.getTypesFromCache(power.getConfiguration().data());
                this.apoliScaleModifier = PehkuiUtil.createApoliScaleModifier(power, living, power.getConfiguration().data());
            } else {
                this.cachedScaleIds = Set.of();
                this.apoliScaleModifier = null;
            }
        }
    }

}
