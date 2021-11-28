package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.ValueModifyingPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.AttributeUtil;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliAttributeUtil;
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
    private final HashMap<ScaleType, Float> originalValues = new HashMap<>();
    private final List<Identifier> scaleIdentifiers = new ArrayList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<ModifyScalePower>(Apugli.identifier("modify_scale"),
                new SerializableData()
                        .add("operation", SerializableDataTypes.IDENTIFIER, new Identifier("pehkui", "set"))
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
        updateOriginalValues();
        if (this.isActive()) {
            addScales();
        } else {
            removeScales();
        }
    }

    public void onRemoved() {
        if (!FabricLoader.getInstance().isModLoaded("pehkui")) {
            runNoPehkuiFoundWarning();
            return;
        }
        removeScales();
    }

    private void updateOriginalValues() {
        if (amountOfModifyScalePowers == PowerHolderComponent.getPowers(entity, ModifyScalePower.class).size()) return;
        this.scaleIdentifiers.forEach(identifier -> {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, identifier);
            ScaleData data = scaleType.getScaleData(entity);
            originalValues.put(scaleType, data.getScale());
        });
        amountOfModifyScalePowers = PowerHolderComponent.getPowers(entity, ModifyScalePower.class).size();
    }

    private void addScales() {
        if (hasChangedSize) return;
        this.scaleIdentifiers.forEach(identifier -> {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, identifier);
            ScaleData data = scaleType.getScaleData(entity);
            float modifiedNewScale = (float) AttributeUtil.applyModifiers(this.getModifiers(), data.getScale());
            data.setTargetScale(modifiedNewScale);
            data.onUpdate();
        });
        hasChangedSize = true;
    }

    private void removeScales() {
        if (!hasChangedSize) return;
        this.scaleIdentifiers.forEach(identifier -> {
            ScaleType scaleType = ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, identifier);
            ScaleData data = scaleType.getScaleData(entity);
            float originalScale = originalValues.get(scaleType);
            float modifiedNewScale = (float) ApugliAttributeUtil.inverseModifiers(this.getModifiers(), data.getScale(), originalScale);
            data.setTargetScale(modifiedNewScale);
            data.onUpdate();
        });
        hasChangedSize = false;
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
