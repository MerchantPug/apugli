package io.github.merchantpug.apugli.behavior.types;

import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.behavior.BehaviorFactory;
import io.github.merchantpug.apugli.behavior.MobBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class PassiveMobBehavior extends MobBehavior {
    public PassiveMobBehavior(int priority) {
        super(priority);
    }

    @Override
    protected void setToDataInstance(SerializableData.Instance dataInstance) {
        super.setToDataInstance(dataInstance);
    }

    @Override
    public boolean isPassive(MobEntity mob, LivingEntity target) {
        return true;
    }

    public static BehaviorFactory<?> getFactory() {
        return new BehaviorFactory<>(Apugli.identifier("passive"),
                new SerializableData(),
                data -> new PassiveMobBehavior(0));
    }
}