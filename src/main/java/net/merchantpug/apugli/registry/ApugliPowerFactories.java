package net.merchantpug.apugli.registry;

import net.merchantpug.apugli.Apugli;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.merchantpug.apugli.power.*;
import net.minecraft.registry.Registry;

public class ApugliPowerFactories {
    public static void register() {
        register(ActionOnBlockPlacedPower.getFactory());
        register(ActionOnBoneMealPower.getFactory());
        register(ActionOnDurabilityChange.getFactory());
        register(ActionOnEquipPower.getFactory());
        register(ActionOnProjectileHitPower.getFactory());
        register(ActionOnTameHitPower.getFactory());
        register(ActionOnTargetDeathPower.getFactory());
        register(ActionWhenTameHitPower.getFactory());
        register(Power.createSimpleFactory(AerialAffinityPower::new, Apugli.identifier("aerial_affinity")));
        register(AllowAnvilEnchantPower.getFactory());
        register(BunnyHopPower.getFactory());
        register(CustomDeathSoundPower.getFactory());
        register(CustomFootstepPower.getFactory());
        register(CustomHurtSoundPower.getFactory());
        register(EdibleItemPower.getFactory());
        register(EnergySwirlPower.getFactory());
        register(EntityTextureOverlayPower.getFactory());
        register(ForceParticleRenderPower.getFactory());
        register(Power.createSimpleFactory(HoverPower::new, Apugli.identifier("hover")));
        register(InstantEffectImmunityPower.getFactory());
        register(Power.createSimpleFactory(InvertInstantEffectsPower::new, Apugli.identifier("invert_instant_effects")));
        register(MobsIgnorePower.getFactory());
        register(ModifyBlockPlacedPower.getFactory());
        register(ModifyBreedingCooldownPower.getFactory());
        register(ModifyEnchantmentDamageDealtPower.getFactory());
        register(ModifyEnchantmentDamageTakenPower.getFactory());
        register(ModifyEnchantmentLevelPower.getFactory());
        register(ModifyEquippedItemRenderPower.getFactory());
        register(ModifySoulSpeedPower.getFactory());
        register(PlayerModelTypePower.getFactory());
        register(Power.createSimpleFactory(PreventBeeAngerPower::new, Apugli.identifier("prevent_bee_anger")));
        register(PreventBreedingPower.getFactory());
        register(PreventLabelRenderPower.getFactory());
        register(PreventSoundPower.getFactory());
        register(ProjectileActionOverTimePower.getFactory());
        register(RedirectLightningPower.getFactory());
        register(RocketJumpPower.getFactory());
        register(SetTexturePower.getFactory());
    }

    public static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
}
