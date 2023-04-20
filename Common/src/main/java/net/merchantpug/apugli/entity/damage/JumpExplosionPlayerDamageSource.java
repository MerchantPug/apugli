package net.merchantpug.apugli.entity.damage;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JumpExplosionPlayerDamageSource extends EntityDamageSource {
    
    public JumpExplosionPlayerDamageSource(LivingEntity attacker) {
        super("jumpExplosion.player", attacker);
        this.setExplosion();
    }
    
    @Override
    public Component getLocalizedDeathMessage(LivingEntity victim) {
        ItemStack weapon = this.entity instanceof LivingEntity attacker ? attacker.getMainHandItem() : ItemStack.EMPTY;
        String string = "death.attack.explosion.player";
        return !weapon.isEmpty() && weapon.hasCustomHoverName() ?
            Component.translatable(string + ".item", victim.getDisplayName(), this.entity.getDisplayName(), weapon.getDisplayName()) :
            Component.translatable(string, victim.getDisplayName(), this.entity.getDisplayName());
    }
    
}
