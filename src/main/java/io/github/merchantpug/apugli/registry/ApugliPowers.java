package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.power.*;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApugliPowers {
    private static final Map<PowerFactory<?>, Identifier> POWER_FACTORIES = new LinkedHashMap<>();

    public static final PowerFactory<Power> APUGLI_ENTITY_GROUP = create(new PowerFactory<>(Apugli.identifier("entity_group"),
            new SerializableData()
                    .add("group", ApugliDataTypes.APUGLI_ENTITY_GROUP),
            data ->
                    (type, entity) ->
                            new SetApugliEntityGroupPower(type, entity, (EntityGroup)data.get("group")))
            .allowCondition());

    public static final PowerFactory<Power> MODIFY_SOUL_SPEED = create(new PowerFactory<>(Apugli.identifier("modify_soul_speed"),
            new SerializableData()
                    .add("modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                    .add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null),
            data ->
                    (type, entity) -> {
                        ModifySoulSpeedPower power = new ModifySoulSpeedPower(type, entity);
                        if(data.isPresent("modifier")) {
                            power.addModifier(data.getModifier("modifier"));
                        }
                        if(data.isPresent("modifiers")) {
                            ((List<EntityAttributeModifier>)data.get("modifiers")).forEach(power::addModifier);
                        }
                        return power;
                    })
            .allowCondition());
    public static final PowerFactory<Power> ENERGY_SWIRL = create(new PowerFactory<>(Apugli.identifier("energy_swirl"),
            new SerializableData()
                    .add("texture_location", SerializableDataTypes.IDENTIFIER)
                    .add("speed", SerializableDataTypes.FLOAT, 0.01F),
            data ->
                    (type, entity) ->
                            new EnergySwirlOverlayPower(type, entity, data.getId("texture_location"), data.getFloat("speed")))
            .allowCondition());
    public static final PowerFactory<Power> WEARABLE_STACK = create(new PowerFactory<>(Apugli.identifier("wearable_stack"),
            new SerializableData()
                    .add("stack", SerializableDataTypes.ITEM_STACK)
                    .add("scale", SerializableDataTypes.FLOAT, 1.0F),
            data ->
                    (type, entity) ->
                            new WearableItemStackPower(type, entity, (ItemStack)data.get("stack"), data.getFloat("scale")))
            .allowCondition());

    public static final PowerFactory<Power> LIGHT_UP_BLOCK = create(new PowerFactory<>(Apugli.identifier("light_up_block"),
            new SerializableData()
                    .add("cooldown", SerializableDataTypes.INT)
                    .add("burn_time", SerializableDataTypes.INT, 1600)
                    .add("brew_time", SerializableDataTypes.INT, 20)
                    .add("particle", SerializableDataTypes.PARTICLE_TYPE, null)
                    .add("particle_count", SerializableDataTypes.INT, 15)
                    .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                    .add("hud_render", ApoliDataTypes.HUD_RENDER)
                    .add("key", ApoliDataTypes.KEY, new Active.Key()),
            data ->
                    (type, entity) -> {
                        LightUpBlockPower power = new LightUpBlockPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), data.getInt("burn_time"), data.getInt("brew_time"), (ParticleType)data.get("particle"), data.getInt("particle_count"), (SoundEvent)data.get("sound"));
                        power.setKey((Active.Key)data.get("key"));
                        return power;
                    })
            .allowCondition());

    public static final PowerFactory<Power> ROCKET_JUMP = create(new PowerFactory<>(Apugli.identifier("rocket_jump"),
            new SerializableData()
                    .add("cooldown", SerializableDataTypes.INT)
                    .add("source", SerializableDataTypes.DAMAGE_SOURCE, null)
                    .add("amount", SerializableDataTypes.FLOAT, 0.0F)
                    .add("speed", SerializableDataTypes.DOUBLE, 1.0)
                    .add("use_charged", SerializableDataTypes.BOOLEAN, false)
                    .add("hud_render", ApoliDataTypes.HUD_RENDER)
                    .add("key", ApoliDataTypes.KEY, new Active.Key()),
            data ->
                    (type, entity) -> {
                        RocketJumpPower power = new RocketJumpPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), (DamageSource)data.get("source"), data.getBoolean("use_charged"), data.getFloat("amount"), data.getDouble("speed"));
                        power.setKey((Active.Key)data.get("key"));
                        return power;
                    })
            .allowCondition());

    public static final PowerFactory<Power> VISUAL_TIMER = create (new PowerFactory<>(Apugli.identifier("visual_timer"),
            new SerializableData()
                    .add("cooldown", SerializableDataTypes.INT)
                    .add("hud_render", ApoliDataTypes.HUD_RENDER)
                    .add("reset_on_respawn", SerializableDataTypes.BOOLEAN),
            data ->
                    (type, entity) ->
                        new VisualTimerPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), data.getBoolean("reset_on_respawn")))
            .allowCondition());

    public static final PowerFactory<Power> BUNNY_HOP = create(new PowerFactory<>(Apugli.identifier("bunny_hop"),
            new SerializableData()
                    .add("cooldown", SerializableDataTypes.INT)
                    .add("increase_per_tick", SerializableDataTypes.DOUBLE, 0.0005)
                    .add("ability_velocity", SerializableDataTypes.INT, 8)
                    .add("max_velocity", SerializableDataTypes.DOUBLE, 0.02)
                    .add("tick_rate", SerializableDataTypes.INT, 10)
                    .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                    .add("hud_render", ApoliDataTypes.HUD_RENDER)
                    .add("key", ApoliDataTypes.KEY, new Active.Key()),
            data ->
                    (type, entity) -> {
                        BunnyHopPower power = new BunnyHopPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), data.getDouble("increase_per_tick"), data.getInt("ability_velocity"), data.getDouble("max_velocity"), (SoundEvent)data.get("sound"), data.getInt("tick_rate"));
                        power.setKey((Active.Key)data.get("key"));
                        return power;
                    })
            .allowCondition());

    public static final PowerFactory<Power> EAT_GRASS = create(new PowerFactory<>(Apugli.identifier("eat_grass"),
            new SerializableData()
                    .add("cooldown", SerializableDataTypes.INT)
                    .add("hud_render", ApoliDataTypes.HUD_RENDER)
                    .add("key", ApoliDataTypes.KEY, new Active.Key()),
            data ->
                    (type, player) -> {
                        EatGrassPower power = new EatGrassPower(type, player, data.getInt("cooldown"), (HudRender)data.get("hud_render"));
                        power.setKey((Active.Key)data.get("key"));
                        return power;
                    }).allowCondition());
    public static final PowerFactory<Power> EDIBLE_STACK = create(new PowerFactory<>(Apugli.identifier("edible_stack"),
            new SerializableData()
                    .add("item_condition", ApoliDataTypes.ITEM_CONDITION)
                    .add("hunger", SerializableDataTypes.INT)
                    .add("saturation", SerializableDataTypes.FLOAT)
                    .add("meat", SerializableDataTypes.BOOLEAN, false)
                    .add("always_edible", SerializableDataTypes.BOOLEAN, false)
                    .add("snack", SerializableDataTypes.BOOLEAN, false)
                    .add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
                    .add("effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null)
                    .add("tick_rate", SerializableDataTypes.INT, 10),
            data ->
                    (type, player) -> {
                        EdibleItemStackPower power = new EdibleItemStackPower(type, player,
                                (ConditionFactory<ItemStack>.Instance)data.get("item_condition"),
                                data.getInt("hunger"),
                                data.getFloat("saturation"),
                                data.getBoolean("meat"),
                                data.getBoolean("always_edible"),
                                data.getBoolean("snack"),
                                data.getInt("tick_rate"));
                        if(data.isPresent("effect")) {
                            power.addEffect((StatusEffectInstance)data.get("effect"));
                        }
                        if(data.isPresent("effects")) {
                            ((List<StatusEffectInstance>)data.get("effects")).forEach(power::addEffect);
                        }
                        return power;
                    })
            .allowCondition());
    public static final PowerFactory<Power> SET_TEXTURE = create(new PowerFactory<>(Apugli.identifier("set_texture"),
            new SerializableData()
                    .add("texture_location", SerializableDataTypes.IDENTIFIER, null),
            data ->
                    (type, player) ->
                        new SetTexturePower(type, player, data.getId("texture_location")))
            .allowCondition());
    public static final PowerFactory<Power> PLAYER_MODEL = create(new PowerFactory<>(Apugli.identifier("player_model"),
            new SerializableData()
                    .add("slim", SerializableDataTypes.BOOLEAN),
            data ->
                    (type, player) ->
                            new SetPlayerModelPower(type, player, data.getBoolean("slim")))
            .allowCondition());

    private static <T extends Power> PowerFactory<T> create(PowerFactory<T> factory) {
        POWER_FACTORIES.put(factory, factory.getSerializerId());
        return factory;
    }

    public static void init() {
        POWER_FACTORIES.keySet().forEach(powerType -> Registry.register(ApoliRegistries.POWER_FACTORY, POWER_FACTORIES.get(powerType), powerType));
    }
}
