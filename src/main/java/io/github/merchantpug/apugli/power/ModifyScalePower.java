package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.AttributeUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;
import virtuoel.pehkui.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModifyScalePower extends ValueModifyingPower {
    private boolean hasAlertedServer = false;
    private boolean hasChangedSize = false;
    private int amountOfModifyScalePowers = 0;
    private final List<Identifier> scaleIdentifiers = new ArrayList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyScalePower>(Apugli.identifier("modify_scale"),
                new SerializableData()
                        .add("scale", SerializableDataTypes.IDENTIFIER, null)
                        .add("scales", SerializableDataTypes.IDENTIFIERS, null)
                        .add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                        .add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null),
                data ->
                        (type, entity) -> {
                            ModifyScalePower power = new ModifyScalePower(type, entity);
                            if(data.isPresent("scale")) {
                                power.addScaleIdentifier(data.getId("scale"));
                            }
                            if(data.isPresent("scales")) {
                                ((List<Identifier>)data.get("scales")).forEach(power::addScaleIdentifier);
                            }
                            if(data.isPresent("modifier")) {
                                power.addModifier(data.getModifier("modifier"));
                            }
                            if(data.isPresent("modifiers")) {
                                ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                            }
                            return power;
                        }).allowCondition();
    }

    public void addScaleIdentifier(Identifier id) {
        this.scaleIdentifiers.add(id);
    }

    public void onAdded() {
        if (!FabricLoader.getInstance().isModLoaded("pehkui")) {
            runNoPehkuiFoundWarning();
        }
    }

    public void tick() {
        if (!FabricLoader.getInstance().isModLoaded("pehkui")) return;
        if (this.isActive()) {
            addScales();
        }
        removeScales();
    }

    public void onRemoved() {
        if (!FabricLoader.getInstance().isModLoaded("pehkui")) {
            runNoPehkuiFoundWarning();
            return;
        }
        removeScales();
    }

    private void addScales() {
        if (hasChangedSize) return;
        this.scaleIdentifiers.forEach(identifier -> {
            ScaleType type = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, identifier);
            ScaleData data = type.getScaleData(entity);
            float modifiedNewScale = PowerHolderComponent.modify(entity, ModifyScalePower.class, type.getDefaultBaseScale());
            data.setTargetScale(modifiedNewScale);
            data.onUpdate();
        });
        hasChangedSize = true;
    }

    private void removeScales() {
        if (!hasChangedSize || amountOfModifyScalePowers == PowerHolderComponent.getPowers(entity, ModifyScalePower.class).size()) return;
        this.scaleIdentifiers.forEach(identifier -> {
            ScaleType type = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, identifier);
            ScaleData data = type.getScaleData(entity);
            data.setTargetScale(type.getDefaultBaseScale());
            data.onUpdate();
        });
        hasChangedSize = false;
        amountOfModifyScalePowers = PowerHolderComponent.getPowers(entity, ModifyScalePower.class).size();
    }

    private void runNoPehkuiFoundWarning() {
        if (this.hasAlertedServer) return;
        Apugli.LOGGER.warn(this.getType().getIdentifier() + " (which uses apugli:modify_scale) will not work. Install Pehkui in order to use this power.");
        this.hasAlertedServer = true;
    }

    public ModifyScalePower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
        this.setTicking(true);
    }
}
