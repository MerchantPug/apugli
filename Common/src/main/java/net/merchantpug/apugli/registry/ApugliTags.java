package net.merchantpug.apugli.registry;

import net.merchantpug.apugli.Apugli;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class ApugliTags {
    public static final TagKey<MobEffect> CHARGED_EFFECTS = TagKey.create(Registries.MOB_EFFECT, Apugli.asResource("charged_effects"));
}
