package net.merchantpug.apugli.power.factory;

import io.github.apace100.calio.data.SerializableData;
import net.merchantpug.apugli.platform.Services;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public interface ActionWhenTameHitPowerFactory<P> extends TameHitActionPowerFactory<P> {

    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
            .add("damage_condition", Services.CONDITION.damageDataType(), null)
            .add("bientity_action", Services.ACTION.biEntityDataType(), null)
            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
            .add("owner_bientity_action", Services.ACTION.biEntityDataType(), null)
            .add("owner_bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }

    default void execute(TamableAnimal tamable, DamageSource source, float amount) {
        if (!(source.getEntity() instanceof LivingEntity living)) return;
        this.execute(tamable, living, tamable, source, amount);
    }
    
}
