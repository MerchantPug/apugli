package net.merchantpug.apugli.registry.power;

import io.github.apace100.apoli.power.Power;
import net.merchantpug.apugli.platform.Services;
import net.merchantpug.apugli.power.*;
import net.merchantpug.apugli.power.factory.*;

import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public class ApugliPowers {

    public static final Supplier<ActionOnAttackerHurtPowerFactory> ACTION_ON_ATTACKER_HURT = register("action_on_attacker_hurt", ActionOnAttackerHurtPowerFactory.class);
    public static final Supplier<ActionOnBlockPlacedPower.Factory> ACTION_ON_BLOCK_PLACED = register(new ActionOnBlockPlacedPower.Factory());
    public static final Supplier<ActionOnBonemealPower.Factory> ACTION_ON_BONEMEAL = register(new ActionOnBonemealPower.Factory());
    public static final Supplier<ActionOnDurabilityChangePower.Factory> ACTION_ON_DURABILITY_CHANGE = register(new ActionOnDurabilityChangePower.Factory());
    public static final Supplier<ActionOnEquipPower.Factory> ACTION_ON_EQUIP = register(new ActionOnEquipPower.Factory());
    public static final Supplier<ActionOnJumpPower.Factory> ACTION_ON_JUMP = register(new ActionOnJumpPower.Factory());
    public static final Supplier<ActionOnProjectileHitPowerFactory> ACTION_ON_PROJECTILE_HIT = register("action_on_projectile_hit", ActionOnProjectileHitPowerFactory.class);
    public static final Supplier<ActionOnHarmPowerFactory> ACTION_ON_HARM = register("action_on_harm", ActionOnHarmPowerFactory.class);
    public static final Supplier<ActionOnTameHitPowerFactory> ACTION_ON_TAME_HIT = register("action_on_tame_hit", ActionOnTameHitPowerFactory.class);
    public static final Supplier<ActionOnTargetDeathPowerFactory> ACTION_ON_TARGET_DEATH = register("action_on_target_death", ActionOnTargetDeathPowerFactory.class);
    public static final Supplier<ActionOnTargetHurtPowerFactory> ACTION_ON_TARGET_HURT = register("action_on_target_hurt", ActionOnTargetHurtPowerFactory.class);
    public static final Supplier<ActionWhenLightningStruckPowerFactory> ACTION_WHEN_LIGHTNING_STRUCK = register("action_when_lightning_struck", ActionWhenLightningStruckPowerFactory.class);
    public static final Supplier<ActionWhenProjectileHitPowerFactory> ACTION_WHEN_PROJECTILE_HIT = register("action_when_projectile_hit", ActionWhenProjectileHitPowerFactory.class);
    public static final Supplier<ActionWhenHarmedPowerFactory> ACTION_WHEN_HARMED = register("action_when_harmed", ActionWhenHarmedPowerFactory.class);
    public static final Supplier<ActionWhenTameHitPowerFactory> ACTION_WHEN_TAME_HIT = register("action_when_tame_hit", ActionWhenTameHitPowerFactory.class);
    public static final Supplier<AllowAnvilEnchantPower.Factory> ALLOW_ANVIL_ENCHANT = register(new AllowAnvilEnchantPower.Factory());
    public static final Supplier<BunnyHopPowerFactory> BUNNY_HOP = register("bunny_hop", BunnyHopPowerFactory.class);
    public static final Supplier<ClientActionOverTime.Factory> CLIENT_ACTION_OVER_TIME = register(new ClientActionOverTime.Factory());
    public static final Supplier<CustomProjectilePowerFactory> CUSTOM_PROJECTILE = register("custom_projectile", CustomProjectilePowerFactory.class);
    public static final Supplier<CustomDeathSoundPower.Factory> CUSTOM_DEATH_SOUND = register(new CustomDeathSoundPower.Factory());
    public static final Supplier<CustomFootstepPower.Factory> CUSTOM_FOOTSTEP = register(new CustomFootstepPower.Factory());
    public static final Supplier<CustomHurtSoundPower.Factory> CUSTOM_HURT_SOUND = register(new CustomHurtSoundPower.Factory());
    public static final Supplier<EdibleItemPower.Factory> EDIBLE_ITEM = register(new EdibleItemPower.Factory());
    public static final Supplier<EnergySwirlPower.Factory> ENERGY_SWIRL = register(new EnergySwirlPower.Factory());
    public static final Supplier<EntityTextureOverlayPower.Factory> ENTITY_TEXTURE_OVERLAY = register(new EntityTextureOverlayPower.Factory());
    public static final Supplier<ForceParticleRenderPower.Factory> FORCE_PARTICLE_RENDER = register(new ForceParticleRenderPower.Factory());
    public static final Supplier<HoverPower.Factory> HOVER = register(new HoverPower.Factory());
    public static final Supplier<InstantEffectImmunityPower.Factory> INSTANT_EFFECT_IMMUNITY = register(new InstantEffectImmunityPower.Factory());
    public static final Supplier<InvertInstantEffectsPower.Factory> INVERT_INSTANT_EFFECTS = register(new InvertInstantEffectsPower.Factory());
    public static final Supplier<MobsIgnorePower.Factory> MOBS_IGNORE = register(new MobsIgnorePower.Factory());
    public static final Supplier<ModifyBlockPlacedPower.Factory> MODIFY_BLOCK_PLACED = register(new ModifyBlockPlacedPower.Factory());
    public static final Supplier<ModifyBreedingCooldownPowerFactory> MODIFY_BREEDING_COOLDOWN = register("modify_breeding_cooldown", ModifyBreedingCooldownPowerFactory.class);
    public static final Supplier<ModifyDurabilityChangePowerFactory> MODIFY_DURABILITY_CHANGE = register("modify_durability_change", ModifyDurabilityChangePowerFactory.class);
    public static final Supplier<ModifyEnchantmentDamageDealtPowerFactory> MODIFY_ENCHANTMENT_DAMAGE_DEALT = register("modify_enchantment_damage_dealt", ModifyEnchantmentDamageDealtPowerFactory.class);
    public static final Supplier<ModifyEnchantmentDamageTakenPowerFactory> MODIFY_ENCHANTMENT_DAMAGE_TAKEN = register("modify_enchantment_damage_taken", ModifyEnchantmentDamageTakenPowerFactory.class);
    public static final Supplier<ModifyEnchantmentLevelPowerFactory> MODIFY_ENCHANTMENT_LEVEL = register("modify_enchantment_level", ModifyEnchantmentLevelPowerFactory.class);
    public static final Supplier<ModifyEquippedItemRenderPower.Factory> MODIFY_EQUIPPED_ITEM_RENDER = register(new ModifyEquippedItemRenderPower.Factory());
    public static final Supplier<ModifyFovPowerFactory> MODIFY_FOV = register("modify_fov", ModifyFovPowerFactory.class);
    public static final Supplier<ModifySoulSpeedPowerFactory> MODIFY_SOUL_SPEED = register("modify_soul_speed", ModifySoulSpeedPowerFactory.class);
    public static final Supplier<PlayerModelTypePower.Factory> PLAYER_MODEL_TYPE = register(new PlayerModelTypePower.Factory());
    public static final Supplier<PreventBeeAngerPower.Factory> PREVENT_BEE_ANGER = register(new PreventBeeAngerPower.Factory());
    public static final Supplier<PreventBreedingPower.Factory> PREVENT_BREEDING = register(new PreventBreedingPower.Factory());
    public static final Supplier<PreventLabelRenderPower.Factory> PREVENT_LABEL_RENDER = register(new PreventLabelRenderPower.Factory());
    public static final Supplier<PreventMovementChecksPower.Factory> PREVENT_MOVEMENT_CHECKS = register(new PreventMovementChecksPower.Factory());
    public static final Supplier<PreventSoundPower.Factory> PREVENT_SOUND = register(new PreventSoundPower.Factory());
    public static final Supplier<ProjectileActionOverTimePower.Factory> PROJECTILE_ACTION_OVER_TIME = register(new ProjectileActionOverTimePower.Factory());
    public static final Supplier<RedirectLightningPower.Factory> REDIRECT_LIGHTNING = register(new RedirectLightningPower.Factory());
    public static final Supplier<SprintingPower.Factory> SPRINTING = register(new SprintingPower.Factory());
    public static final Supplier<StepHeightPower.Factory> STEP_HEIGHT = register(new StepHeightPower.Factory());

    @Deprecated
    public static final Supplier<AerialAffinityPowerFactory> AERIAL_AFFINITY = register("aerial_affinity", AerialAffinityPowerFactory.class);
    @Deprecated
    public static final Supplier<RocketJumpPowerFactory> ROCKET_JUMP = register("rocket_jump", RocketJumpPowerFactory.class);
    @Deprecated
    public static final Supplier<SetTexturePower.Factory> SET_TEXTURE = register(new SetTexturePower.Factory());

    public static <P extends Power, F extends SimplePowerFactory<P>> Supplier<F> register(F factory) {
        return Services.POWER.registerFactory(factory);
    }
    
    public static <F extends SpecialPowerFactory> Supplier<F> register(String name, Class<F> iface) {
        return Services.POWER.registerFactory(name, iface);
    }
    
    public static void registerAll() {}
    
}
