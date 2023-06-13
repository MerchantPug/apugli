package net.merchantpug.apugli.power;

import com.google.auto.service.AutoService;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.power.factory.ActionOnAttackerHurtPowerFactory;
import net.merchantpug.apugli.power.factory.TargetHurtActionPowerFactory;
import net.minecraft.world.entity.LivingEntity;

@AutoService(ActionOnAttackerHurtPowerFactory.class)
public class ActionOnAttackerHurtPower extends AbstractCooldownPower<ActionOnAttackerHurtPower.Instance> implements ActionOnAttackerHurtPowerFactory<ActionOnAttackerHurtPower.Instance> {

    public ActionOnAttackerHurtPower() {
        super("action_on_attacker_hurt", TargetHurtActionPowerFactory.getSerializableData(),
                data -> (type, entity) -> new ActionOnAttackerHurtPower.Instance(type, entity, data));
        allowCondition();
    }

    @Override
    public Class<ActionOnAttackerHurtPower.Instance> getPowerClass() {
        return ActionOnAttackerHurtPower.Instance.class;
    }

    public static class Instance extends AbstractCooldownPower.Instance {

        public Instance(PowerType<?> type, LivingEntity entity, SerializableData.Instance data) {
            super(type, entity, data);
        }

    }

}
