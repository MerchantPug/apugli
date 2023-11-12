package net.merchantpug.apugli.power.factory;

import net.merchantpug.apugli.platform.Services;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public interface TameHitActionPowerFactory<P> extends CooldownPowerFactory<P> {
    
    static SerializableData getSerializableData() {
        return CooldownPowerFactory.getSerializableData()
            .add("damage_condition", Services.CONDITION.damageDataType(), null)
            .add("bientity_action", Services.ACTION.biEntityDataType(), null)
            .add("bientity_condition", Services.CONDITION.biEntityDataType(), null)
            .add("owner_bientity_action", Services.ACTION.biEntityDataType(), null)
            .add("owner_bientity_condition", Services.CONDITION.biEntityDataType(), null);
    }
    
    default void execute(TamableAnimal tamable, LivingEntity attacker, LivingEntity target, DamageSource source, float amount) {
        LivingEntity owner = tamable.getOwner();
        if(owner == null) return;
        LivingEntity ownerBientityLeft = tamable == target ? attacker : owner;
        LivingEntity ownerBientityRight = tamable == target ? owner : attacker;

        for(P power : Services.POWER.getPowers(owner, this)) {
            if(canUse(power, owner)) {
                SerializableData.Instance data = getDataFromPower(power);
                if(Services.CONDITION.checkDamage(data, "damage_condition", source, amount) &&
                   Services.CONDITION.checkBiEntity(data, "bientity_condition", attacker, target) &&
                   Services.CONDITION.checkBiEntity(data, "owner_bientity_condition", ownerBientityLeft, ownerBientityRight)
                ) {
                    Services.ACTION.executeBiEntity(data, "bientity_action", attacker, target);
                    Services.ACTION.executeBiEntity(data, "owner_bientity_action", ownerBientityLeft, ownerBientityRight);
                    use(power, owner);
                }
            }
        }
    }
    
}
