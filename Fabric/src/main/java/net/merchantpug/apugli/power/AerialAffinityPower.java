package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.ModifyBreakSpeedPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.Apugli;
import net.merchantpug.apugli.power.factory.AerialAffinityPowerFactory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

@Deprecated
@AutoService(AerialAffinityPowerFactory.class)
public class AerialAffinityPower extends PowerFactory<ModifyBreakSpeedPower> implements AerialAffinityPowerFactory<ModifyBreakSpeedPower> {

    public AerialAffinityPower() {
        super(Apugli.asResource("aerial_affinity"), AerialAffinityPowerFactory.getSerializableData(),
            data -> (type, entity) -> {
                ModifyBreakSpeedPower power = new ModifyBreakSpeedPower(type, entity, blockInWorld -> true);
                power.addModifier(new AttributeModifier("Aerial affinity break speed increase", 4.0, AttributeModifier.Operation.MULTIPLY_BASE));
                power.addCondition(e -> !e.isOnGround());
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
