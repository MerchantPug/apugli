package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.ModifyBreakSpeedPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.modifier.Modifier;
import io.github.apace100.apoli.util.modifier.ModifierOperation;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.factory.AerialAffinityPowerFactory;

@Deprecated
@AutoService(AerialAffinityPowerFactory.class)
public class AerialAffinityPower extends PowerFactory<ModifyBreakSpeedPower> implements AerialAffinityPowerFactory<ModifyBreakSpeedPower> {

    public AerialAffinityPower() {
        super(Apugli.asResource("aerial_affinity"), AerialAffinityPowerFactory.getSerializableData(),
            data -> (type, entity) -> {
                ModifyBreakSpeedPower power = new ModifyBreakSpeedPower(type, entity, blockInWorld -> true);

                SerializableData.Instance modifierData = ModifierOperation.DATA.new Instance();
                modifierData.set("value", 4.0);
                modifierData.set("resource", null);
                modifierData.set("modifier", null);
                power.addModifier(new Modifier(ModifierOperation.MULTIPLY_BASE_MULTIPLICATIVE, modifierData));
                power.addCondition(e -> !e.onGround());

                return power;
            });
        allowCondition();
    }

    @Override
    public SerializableData.Instance getDataFromPower(ModifyBreakSpeedPower power) {
        return AerialAffinityPowerFactory.getSerializableData().new Instance();
    }

    @Override
    public Class<ModifyBreakSpeedPower> getPowerClass() {
        return ModifyBreakSpeedPower.class;
    }
    
}
