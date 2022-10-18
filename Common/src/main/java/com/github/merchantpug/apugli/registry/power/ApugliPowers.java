package com.github.merchantpug.apugli.registry.power;

import com.github.merchantpug.apugli.platform.Services;
import com.github.merchantpug.apugli.power.*;
import com.github.merchantpug.apugli.power.factory.*;
import io.github.apace100.apoli.power.Power;

import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public class ApugliPowers {
    
    public static final Supplier<ActionOnBlockPlacedPower.Factory> ACTION_ON_BLOCK_PLACED = register(new ActionOnBlockPlacedPower.Factory());
    public static final Supplier<ActionOnBonemealPower.Factory> ACTION_ON_BONEMEAL = register(new ActionOnBonemealPower.Factory());
    public static final Supplier<ActionOnDurabilityChangePower.Factory> ACTION_ON_DURABILITY_CHANGE = register(new ActionOnDurabilityChangePower.Factory());
    public static final Supplier<ActionOnEquipPower.Factory> ACTION_ON_EQUIP = register(new ActionOnEquipPower.Factory());
    public static final Supplier<ActionOnTargetDeathPowerFactory> ACTION_ON_TARGET_DEATH = register("action_on_target_death", ActionOnTargetDeathPowerFactory.class);
    public static final Supplier<ActionWhenTameHitPowerFactory> ACTION_WHEN_TAME_HIT = register("action_when_tame_hit", ActionWhenTameHitPowerFactory.class);
    public static final Supplier<AerialAffinityPower.Factory> AERIAL_AFFINITY = register(new AerialAffinityPower.Factory());
    public static final Supplier<AllowAnvilEnchantPower.Factory> ALLOW_ANVIL_ENCHANT = register(new AllowAnvilEnchantPower.Factory());
    public static final Supplier<BunnyHopPowerFactory> BUNNY_HOP = register("bunny_hop", BunnyHopPowerFactory.class);
    public static final Supplier<CustomDeathSoundPower.Factory> CUSTOM_DEATH_SOUND = register(new CustomDeathSoundPower.Factory());
    public static final Supplier<CustomFootstepPower.Factory> CUSTOM_FOOD_STEP = register(new CustomFootstepPower.Factory());
    public static final Supplier<CustomHurtSoundPower.Factory> CUSTOM_HURT_SOUND = register(new CustomHurtSoundPower.Factory());
    public static final Supplier<EdibleItemPower.Factory> EDIBLE_ITEM_POWER = register(new EdibleItemPower.Factory());
    
    public static <P extends Power, F extends SimplePowerFactory<P>> Supplier<F> register(F factory) {
        return Services.POWER.registerFactory(factory);
    }
    
    public static <F extends SpecialPowerFactory> Supplier<F> register(String name, Class<F> iface) {
        return Services.POWER.registerFactory(name, iface);
    }
    
    public static void registerAll() {}
    
}
