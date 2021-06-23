package io.github.merchantpug.apugli.registry;

import io.github.apace100.calio.mixin.DamageSourceAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;

public class ApugliDamageSources {

    public static DamageSource zombification(Entity attacker) {
        return ((DamageSourceAccessor)((DamageSourceAccessor)new EntityDamageSource("indirectZombification", attacker)).callSetBypassesArmor()).callSetUnblockable();
    }

    public static DamageSource jumpExplosion(LivingEntity attacker) {
        return attacker != null ? (new EntityDamageSource("jumpExplosion.player", attacker)).setExplosive() : ((DamageSourceAccessor.createDamageSource("jumpExplosion")).setExplosive());
    }
}
