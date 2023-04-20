package net.merchantpug.apugli.entity.damage;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

@MethodsReturnNonnullByDefault
public class JumpExplosionDamageSource extends DamageSource {
    
    public JumpExplosionDamageSource() {
        super("jumpExplosion");
        this.setExplosion();
    }
    
    @Override
    public Component getLocalizedDeathMessage(LivingEntity victim) {
        LivingEntity attacker = victim.getKillCredit();
        String key = "death.attack.explosion";
        return attacker != null ?
            Component.translatable(key + ".player", victim.getDisplayName(), attacker.getDisplayName()) :
            Component.translatable(key, victim.getDisplayName());
    }
    
}
