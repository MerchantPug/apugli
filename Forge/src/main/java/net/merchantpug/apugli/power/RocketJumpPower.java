package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.calio.data.SerializableData;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import net.merchantpug.apugli.power.configuration.FabricActiveCooldownConfiguration;
import net.merchantpug.apugli.power.factory.RocketJumpPowerFactory;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@AutoService(RocketJumpPowerFactory.class)
public class RocketJumpPower extends AbstractActiveCooldownPower implements RocketJumpPowerFactory<ConfiguredPower<FabricActiveCooldownConfiguration, ?>> {

    public RocketJumpPower() {
        super(RocketJumpPowerFactory.getSerializableData().xmap(
                FabricActiveCooldownConfiguration::new,
                FabricActiveCooldownConfiguration::data
        ).codec());
    }

    @Override
    public void activate(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity player) {
        if (this.canUse(power, player)) {
            this.execute(power, player);
        }
    }

    @Override
    public void execute(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        executeJump(power, entity);
    }

    @Override
    public SerializableData.Instance getDataFromPower(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power) {
        return power.getConfiguration().data();
    }

    @Override
    public List<ConfiguredModifier<?>> chargedModifiers(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        SerializableData.Instance data = getDataFromPower(power);
        List<ConfiguredModifier<?>> modifiers = new ArrayList<>();
        data.<List<ConfiguredModifier<?>>>ifPresent("charged_modifiers", modifiers::addAll);
        data.<ConfiguredModifier<?>>ifPresent("charged_modifier", modifiers::add);
        return modifiers;
    }

    @Override
    public List<?> waterModifiers(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        SerializableData.Instance data = getDataFromPower(power);
        List<ConfiguredModifier<?>> modifiers = new ArrayList<>();
        data.<List<ConfiguredModifier<?>>>ifPresent("water_modifiers", modifiers::addAll);
        data.<ConfiguredModifier<?>>ifPresent("water_modifier", modifiers::add);
        return modifiers;
    }

    @Override
    public List<?> damageModifiers(ConfiguredPower<FabricActiveCooldownConfiguration, ?> power, Entity entity) {
        SerializableData.Instance data = getDataFromPower(power);
        List<ConfiguredModifier<?>> modifiers = new ArrayList<>();
        data.<List<ConfiguredModifier<?>>>ifPresent("damage_modifiers", modifiers::addAll);
        data.<ConfiguredModifier<?>>ifPresent("damage_modifier", modifiers::add);
        return modifiers;
    }

}
