package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.power.*;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
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

    public static final PowerFactory<Power> EXTRA_SOUL_SPEED = create(new PowerFactory<>(Apugli.identifier("extra_soul_speed"),
            new SerializableData()
                    .add("modifier", SerializableDataTypes.INT),
            data ->
                    (type, entity) ->
                            new ExtraSoulSpeedPower(type, entity, data.getInt("modifier")))
            .allowCondition());
    public static final PowerFactory<Power> ENERGY_SWIRL = create(new PowerFactory<>(Apugli.identifier("energy_swirl"),
            new SerializableData()
                    .add("texture_location", ApoliDataTypes.APOLI_IDENTIFIER)
                    .add("speed", SerializableDataTypes.FLOAT, 0.01F),
            data ->
                    (type, entity) ->
                            new EnergySwirlOverlayPower(type, entity, data.getId("texture_location"), data.getFloat("speed")))
            .allowCondition());
    public static final PowerFactory<Power> UNENCHANTED_SOUL_SPEED = create(new PowerFactory<>(Apugli.identifier("unenchanted_soul_speed"),
            new SerializableData().add("modifier", SerializableDataTypes.INT),
            data ->
                    (type, entity) ->
                            new UnenchantedSoulSpeedPower(type, entity, data.getInt("modifier"))));

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
                    .add("damage_source", SerializableDataTypes.DAMAGE_SOURCE, DamageSource.GENERIC)
                    .add("damage_amount", SerializableDataTypes.FLOAT, 3.0F)
                    .add("speed", SerializableDataTypes.DOUBLE, 1.0)
                    .add("should_use_charged", SerializableDataTypes.BOOLEAN, false)
                    .add("hud_render", ApoliDataTypes.HUD_RENDER)
                    .add("key", ApoliDataTypes.KEY, new Active.Key()),
            data ->
                    (type, entity) -> {
                        RocketJumpPower power = new RocketJumpPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), (DamageSource)data.get("damage_source"), data.getBoolean("should_use_charged"), data.getFloat("damage_amount"), data.getDouble("speed"));
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
                    (type, entity) -> {
                        VisualTimerPower power = new VisualTimerPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), data.getBoolean("reset_on_respawn"));
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

    public static final PowerFactory<Power> DETONATE = create(new PowerFactory<>(Apugli.identifier("detonate"),
            new SerializableData()
                    .add("explosion_radius", SerializableDataTypes.FLOAT, 3.0F)
                    .add("spawns_effect_cloud", SerializableDataTypes.BOOLEAN, false)
                    .add("damage_source", SerializableDataTypes.DAMAGE_SOURCE, DamageSource.GENERIC)
                    .add("self_damage_source", SerializableDataTypes.DAMAGE_SOURCE, DamageSource.OUT_OF_WORLD)
                    .add("key", ApoliDataTypes.KEY, new Active.Key()),
            data ->
                    (type, player) -> {
                        DetonatePower power = new DetonatePower(type, player, data.getFloat("explosion_radius"), data.getBoolean("spawns_effect_cloud"), (DamageSource)data.get("damage_source"), (DamageSource)data.get("self_damage_source"));
                        power.setKey((Active.Key)data.get("key"));
                        return power;
                    }));

    private static <T extends Power> PowerFactory<T> create(PowerFactory<T> factory) {
        POWER_FACTORIES.put(factory, factory.getSerializerId());
        return factory;
    }

    public static void init() {
        POWER_FACTORIES.keySet().forEach(powerType -> Registry.register(ApoliRegistries.POWER_FACTORY, POWER_FACTORIES.get(powerType), powerType));
    }
}
