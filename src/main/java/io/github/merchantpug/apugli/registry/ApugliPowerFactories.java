package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.power.*;
import net.minecraft.util.registry.Registry;

public class ApugliPowerFactories {
    public static void register() {
        register(ActionOnBlockPlacedPower.getFactory());
        register(ActionOnEquipPower.getFactory());
        register(ActionOnDurabilityChange.getFactory());
        register(ActionOnBoneMealPower.getFactory());
        register(Power.createSimpleFactory(AerialAffinityPower::new, Apugli.identifier("aerial_affinity")));
        register(AllowAnvilEnchantPower.getFactory());
        register(BunnyHopPower.getFactory());
        register(CustomFootstepPower.getFactory());
        register(CustomDeathSoundPower.getFactory());
        register(CustomHurtSoundPower.getFactory());
        register(EdibleItemPower.getFactory());
        register(EffectWhitelistPower.getFactory());
        register(EnergySwirlPower.getFactory());
        register(ForceParticleRenderPower.getFactory());
        register(HoverPower.getFactory());
        register(Power.createSimpleFactory(InvertInstantEffectsPower::new, Apugli.identifier("invert_instant_effects")));
        register(ModifyBlockPlacedPower.getFactory());
        register(ModifyBreedingCooldownPower.getFactory());
        register(ModifyEnchantmentDamageDealtPower.getFactory());
        register(ModifyEnchantmentDamageTakenPower.getFactory());
        register(ModifyEnchantmentLevelPower.getFactory());
        register(ModifyEquippedItemRenderPower.getFactory());
        register(ModifyMobBehaviorPower.getFactory());
        register(ModifySoulSpeedPower.getFactory());
        register(Power.createSimpleFactory(PreventBeeAngerPower::new, Apugli.identifier("prevent_bee_anger")));
        register(PreventBreedingPower.getFactory());
        register(PreventSoundPower.getFactory());
        register(PreventLabelRenderPower.getFactory());
        register(RocketJumpPower.getFactory());
        register(SetTexturePower.getFactory());
    }

    public static void register(PowerFactory<?> serializer) {
        Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
    }
}
