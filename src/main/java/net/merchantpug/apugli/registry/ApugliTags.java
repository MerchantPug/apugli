package net.merchantpug.apugli.registry;

import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class ApugliTags {
    public static final TagKey<StatusEffect> CHARGED_EFFECTS = TagKey.of(Registry.MOB_EFFECT_KEY, Apugli.identifier("charged_effects"));
}
