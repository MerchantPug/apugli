package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ActionOnAttackerHurtPowerFactory;
import net.merchantpug.apugli.power.factory.ActionOnTargetHurtPowerFactory;
import net.merchantpug.apugli.power.factory.TargetHurtActionPowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ActionOnTargetHurtPowerFactory.class)
public class ActionOnTargetHurtPower extends AbstractCooldownPower<ActionOnTargetHurtPower.Instance> implements ActionOnTargetHurtPowerFactory<ActionOnTargetHurtPower.Instance> {

    public ActionOnTargetHurtPower() {
        super("action_on_target_hurt", TargetHurtActionPowerFactory.getSerializableData(),
                data -> (type, entity) -> new ActionOnTargetHurtPower.Instance(type, entity, data));
        allowCondition();
    }

    @Override
    public Class<ActionOnTargetHurtPower.Instance> getPowerClass() {
        return ActionOnTargetHurtPower.Instance.class;
    }

    public static class Instance extends AbstractCooldownPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

    }

}
