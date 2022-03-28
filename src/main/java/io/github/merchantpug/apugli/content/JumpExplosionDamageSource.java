package io.github.merchantpug.apugli.content;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

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
        return lv != null ? new TranslatableText(string2, new Object[]{arg.getDisplayName(), lv.getDisplayName()}) : new TranslatableText(string, new Object[]{arg.getDisplayName()});
    }
}
