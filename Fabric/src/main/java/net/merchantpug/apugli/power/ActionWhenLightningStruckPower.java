package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ActionWhenLightningStruckPowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ActionWhenLightningStruckPowerFactory.class)
public class ActionWhenLightningStruckPower extends AbstractCooldownPower<ActionWhenLightningStruckPower.Instance> implements ActionWhenLightningStruckPowerFactory<ActionWhenLightningStruckPower.Instance> {

    public ActionWhenLightningStruckPower() {
        super("action_when_lightning_struck", ActionWhenLightningStruckPowerFactory.getSerializableData(),
                data -> (type, entity) -> new ActionWhenLightningStruckPower.Instance(type, entity, data));
        allowCondition();
    }

    @Override
    public Class<ActionWhenLightningStruckPower.Instance> getPowerClass() {
        return ActionWhenLightningStruckPower.Instance.class;
    }

    public static class Instance extends AbstractCooldownPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

    }
}
