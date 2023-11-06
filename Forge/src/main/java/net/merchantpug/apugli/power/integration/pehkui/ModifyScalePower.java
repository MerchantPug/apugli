package net.merchantpug.apugli.power.integration.pehkui;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
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
    private static final Set<ResourceLocation> EMPTY_SET = new HashSet<>();
    private static final Map<Entity, Map<ConfiguredPower<FabricValueModifyingConfiguration, ?>, Set<ResourceLocation>>> TYPE_CACHE = new HashMap<>();

    public ModifyScalePower() {
        super(ModifyScalePowerFactory.getSerializableData().xmap(
                FabricValueModifyingConfiguration::new,
                FabricValueModifyingConfiguration::data
        ).codec());
        this.ticking(true);
    }

    @Override
    public void tick(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            PehkuiUtil.tickScalePower(power, living);
    }

    @Override
    public void onAdded(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            PehkuiUtil.onAddedScalePower(power, living);
    }

    @Override
    public void onRemoved(ConfiguredPower<FabricValueModifyingConfiguration, ?> power, Entity entity) {
        if (entity instanceof LivingEntity living)
            PehkuiUtil.onRemovedScalePower(power, living);
    }

    @Override
    public void serialize(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuration, IPowerContainer container, CompoundTag tag) {
        if (container.getOwner() instanceof LivingEntity living)
            PehkuiUtil.scalePowerToTag(configuration, living, tag);
    }

    @Override
    public void deserialize(ConfiguredPower<FabricValueModifyingConfiguration, ?> configuration, IPowerContainer container, CompoundTag tag) {
        if (container.getOwner() instanceof LivingEntity living)
            PehkuiUtil.scalePowerFromTag(configuration, living, tag);
    }

    @Override
    public ResourceLocation getPowerId(ConfiguredPower<FabricValueModifyingConfiguration, ?> power) {
        return power.getRegistryName();
    }

}
