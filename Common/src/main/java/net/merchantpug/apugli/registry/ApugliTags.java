package net.merchantpug.apugli.registry;

import net.merchantpug.apugli.Apugli;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class ApugliTags {
    public static final TagKey<MobEffect> CHARGED_EFFECTS = TagKey.create(Registry.MOB_EFFECT_REGISTRY, Apugli.asResource("charged_effects"));
}
