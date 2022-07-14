package com.github.merchantpug.apugli.registry;

import com.github.merchantpug.apugli.content.JumpExplosionDamageSource;
import com.github.merchantpug.apugli.content.JumpExplosionPlayerDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

public class ApugliDamageSources {
    public static DamageSource jumpExplosion(LivingEntity attacker) {
        return attacker != null ? new JumpExplosionPlayerDamageSource(attacker) : new JumpExplosionDamageSource();
    }
}
