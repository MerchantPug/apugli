package io.github.merchantpug.apugli.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.powers.ModifyBreedingCooldownPower;
import io.github.merchantpug.apugli.powers.PreventBreedingPower;
import io.github.merchantpug.apugli.powers.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

@SuppressWarnings({"unchecked", "UnstableApiUsage", "deprecation"})
public class ApugliPowerFactories {
    public static void register() {
        register(ActionOnBlockPlacedPower.getFactory());
        register(ActionOnBoneMealPower.getFactory());
        register(ActionOnDurabilityChange.getFactory());
        register(ActionOnEquipPower.getFactory());
        register(getSimpleFactory(AerialAffinityPower::new, Apugli.identifier("aerial_affinity")));
        register(AllowAnvilEnchantPower.getFactory());
        register(BunnyHopPower.getFactory());
        register(CustomDeathSoundPower.getFactory());
        register(CustomFootstepPower.getFactory());
        register(CustomHurtSoundPower.getFactory());
        register(EdibleItemPower.getFactory());
        register(EffectWhitelistPower.getFactory());
        register(EnergySwirlPower.getFactory());
        register(ForceParticleRenderPower.getFactory());
        register(HoverPower.getFactory());
        register(InvertedCooldownPower.getFactory());
        register(getSimpleFactory(InvertInstantEffectsPower::new, Apugli.identifier("invert_instant_effects")));
        register(MobsIgnorePower.getFactory());
        register(ModifyBlockPlacedPower.getFactory());
        register(ModifyBreedingCooldownPower.getFactory());
        register(ModifyEnchantmentDamageDealtPower.getFactory());
        register(ModifyEnchantmentDamageTakenPower.getFactory());
        register(ModifyEnchantmentLevelPower.getFactory());
        register(ModifyEquippedItemRenderPower.getFactory());
        register(ModifySoulSpeedPower.getFactory());
        register(ModifyStatusEffectAmplifierPower.getFactory());
        register(ModifyStatusEffectDurationPower.getFactory());
        register(getSimpleFactory(PreventBeeAngerPower::new, Apugli.identifier("prevent_bee_anger")));
        register(PreventBreedingPower.getFactory());
        register(PreventLabelRenderPower.getFactory());
        register(PreventSoundPower.getFactory());
        register(RocketJumpPower.getFactory());
        register(SetTexturePower.getFactory());
    }

    public static PowerFactory<?> getSimpleFactory(BiFunction<PowerType<?>, PlayerEntity, Power> powerConstructor, Identifier identifier) {
        return new PowerFactory<>(identifier,
                new SerializableData(), data -> powerConstructor::apply).allowCondition();
    }

    @ExpectPlatform
    private static void register(PowerFactory<?> serializer) {
        throw new AssertionError();
    }
}
