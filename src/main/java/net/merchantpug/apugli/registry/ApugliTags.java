package net.merchantpug.apugli.registry;

import net.merchantpug.apugli.Apugli;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class ApugliTags {
    public static final TagKey<StatusEffect> CHARGED_EFFECTS = TagKey.of(RegistryKeys.STATUS_EFFECT, Apugli.identifier("charged_effects"));
}
