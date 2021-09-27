package io.github.merchantpug.apugli.registry;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.power.*;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
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
                    .add("modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                    .add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null),
            data ->
                    (type, entity) -> {
                        ModifySoulSpeedPower power = new ModifySoulSpeedPower(type, entity, (ConditionFactory< CachedBlockPosition >.Instance)data.get("block_condition"));
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
    public static final PowerFactory<Power> ROCKET_JUMP = create(new PowerFactory<>(Apugli.identifier("rocket_jump"),
            new SerializableData()
                    .add("cooldown", SerializableDataTypes.INT)
                    .add("source", SerializableDataTypes.DAMAGE_SOURCE, null)
                    .add("amount", SerializableDataTypes.FLOAT, 0.0F)
                    .add("speed", SerializableDataTypes.DOUBLE, 1.0D)
                    .add("use_charged", SerializableDataTypes.BOOLEAN, false)
                    .add("charged_modifier", SerializableDataTypes.ATTRIBUTE_MODIFIER, null)
                    .add("charged_modifiers", SerializableDataTypes.ATTRIBUTE_MODIFIERS, null)
                    .add("hud_render", ApoliDataTypes.HUD_RENDER)
                    .add("key", ApoliDataTypes.KEY, new Active.Key()),
            (data) ->
                    (type, entity) ->  {
                        RocketJumpPower power = new RocketJumpPower(type, entity, data.getInt("cooldown"), (HudRender)data.get("hud_render"), (DamageSource)data.get("source"), data.getFloat("amount"), data.getDouble("speed"), data.getBoolean("use_charged"));
                        power.setKey((Active.Key)data.get("key"));
                        if(data.isPresent("charged_modifier")) {
                            power.addChargedJumpModifier(data.getModifier("charged_modifier"));
                        }
                        if(data.isPresent("charged_modifiers")) {
                            ((List<EntityAttributeModifier>)data.get("charged_modifiers")).forEach(power::addChargedJumpModifier);
                        }
                        return power;
                    })
            .allowCondition());

    public static final PowerFactory<Power> MODIFY_EQUIPPED_ITEM_RENDER = create(new PowerFactory<>(Apugli.identifier("modify_equipped_item_render"),
            new SerializableData()
                    .add("equipment_slot", SerializableDataTypes.EQUIPMENT_SLOT)
                    .add("stack", SerializableDataTypes.ITEM_STACK)
                    .add("scale", SerializableDataTypes.FLOAT, 1.0F)
                    .add("render_equipped", SerializableDataTypes.BOOLEAN, true),
            data ->
                    (type, entity) ->
                            new ModifyEquippedItemRenderPower(type, entity, (EquipmentSlot)data.get("equipment_slot"), (ItemStack)data.get("stack"), data.getFloat("scale"), data.getBoolean("render_equipped")))
            .allowCondition());

    public static final PowerFactory<Power> BUNNY_HOP = create(new PowerFactory<>(Apugli.identifier("bunny_hop"),
            new SerializableData()
                    .add("cooldown", SerializableDataTypes.INT)
                    .add("increase_per_tick", SerializableDataTypes.DOUBLE, 0.000375)
                    .add("ability_velocity", SerializableDataTypes.INT, 5)
                    .add("max_velocity", SerializableDataTypes.DOUBLE, 0.015)
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

    public static final PowerFactory<Power> EDIBLE_ITEM = create(new PowerFactory<>(Apugli.identifier("edible_item"),
            new SerializableData()
                    .add("item_condition", ApoliDataTypes.ITEM_CONDITION)
                    .add("food_component", ApugliDataTypes.FOOD_COMPONENT)
                    .add("use_action", ApugliDataTypes.EAT_ACTION, null)
                    .add("return_stack", SerializableDataTypes.ITEM_STACK, null)
                    .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                    .add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
                    .add("tick_rate", SerializableDataTypes.INT, 10),
            data ->
                    (type, player) -> {
                        return new EdibleItemPower(type, player,
                                (ConditionFactory<ItemStack>.Instance)data.get("item_condition"),
                                (FoodComponent)data.get("food_component"),
                                (UseAction)data.get("use_action"),
                                (ItemStack)data.get("return_stack"),
                                (SoundEvent)data.get("sound"),
                                (ActionFactory<Entity>.Instance)data.get("entity_action"),
                                data.getInt("tick_rate"));
                    })
            .allowCondition());

    public static final PowerFactory<Power> SET_TEXTURE = create(new PowerFactory<>(Apugli.identifier("set_texture"),
            new SerializableData()
                    .add("texture_location", SerializableDataTypes.IDENTIFIER, null)
                    .add("player_model", SerializableDataTypes.STRING, null),
            data ->
                    (type, player) ->
                        new SetTexturePower(type, player, data.getId("texture_location"), data.getString("player_model")))
            .allowCondition());

    private static <T extends Power> PowerFactory<T> create(PowerFactory<T> factory) {
        POWER_FACTORIES.put(factory, factory.getSerializerId());
        return factory;
    }

    public static void init() {
        POWER_FACTORIES.keySet().forEach(powerType -> Registry.register(ApoliRegistries.POWER_FACTORY, POWER_FACTORIES.get(powerType), powerType));
    }
}
