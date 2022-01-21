package io.github.merchantpug.apugli.registry;

import io.github.merchantpug.apugli.content.effect.ChargedStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ApugliStatusEffects {
    public static final StatusEffect CHARGED = new ChargedStatusEffect();

    public static void register() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier("toomanyorigins", "charged"), CHARGED);
    }
}
