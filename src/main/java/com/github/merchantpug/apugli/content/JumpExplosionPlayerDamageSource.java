package com.github.merchantpug.apugli.content;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class JumpExplosionPlayerDamageSource extends EntityDamageSource {
    public JumpExplosionPlayerDamageSource(LivingEntity attacker) {
        super("jumpExplosion.player", attacker);
        this.setExplosive();
    }

    @Override
    public Text getDeathMessage(LivingEntity arg) {
        ItemStack lv = this.source instanceof LivingEntity ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack.explosion.player";
        return !lv.isEmpty() && lv.hasCustomName() ? Text.translatable(string + ".item", arg.getDisplayName(), this.source.getDisplayName(), lv.toHoverableText()) : Text.translatable(string, arg.getDisplayName(), this.source.getDisplayName());
    }
}
