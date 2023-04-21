package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public interface ActionWhenHarmedPowerFactory<P> extends HarmActionPowerFactory<P> {

    static SerializableData getSerializableData() {
        return HarmActionPowerFactory.getSerializableData();
    }

    default void execute(P power, LivingEntity entity, DamageSource source, float amount) {
        this.execute(power, entity, source, amount, entity.getLastHurtByMob(), entity);
    }

}
