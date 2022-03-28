package io.github.merchantpug.apugli.content;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class JumpExplosionPlayerDamageSource extends EntityDamageSource {
    public JumpExplosionPlayerDamageSource(LivingEntity attacker) {
        super("jumpExplosion.player", attacker);
        this.setExplosive();
    }

    @Override
    public Text getDeathMessage(LivingEntity arg) {
        ItemStack lv = this.source instanceof LivingEntity ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack.explosion.player";
        return !lv.isEmpty() && lv.hasCustomName() ? new TranslatableText(string + ".item", new Object[]{arg.getDisplayName(), this.source.getDisplayName(), lv.toHoverableText()}) : new TranslatableText(string, new Object[]{arg.getDisplayName(), this.source.getDisplayName()});
    }
}
