package net.merchantpug.apugli.content;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;

public class JumpExplosionDamageSource extends DamageSource {
    public JumpExplosionDamageSource() {
        super("jumpExplosion");
        this.setExplosive();
    }

    @Override
    public Text getDeathMessage(LivingEntity arg) {
        LivingEntity lv = arg.getPrimeAdversary();
        String string = "death.attack.explosion";
        String string2 = string + ".player";
        return lv != null ? Text.translatable(string2, arg.getDisplayName(), lv.getDisplayName()) : Text.translatable(string, arg.getDisplayName());
    }
}
