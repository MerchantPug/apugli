/*
MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package io.github.merchantpug.apugli.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.item.FoodComponent;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Hand;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;

import static io.github.apace100.origins.util.SerializableDataType.PARTICLE_TYPE;
import static io.github.apace100.origins.util.SerializableDataType.STRING;

public class BackportedDataTypes {
    public static final SerializableDataType<BlockState> BLOCK_STATE = SerializableDataType.wrap(BlockState.class, STRING,
            BlockArgumentParser::stringifyBlockState,
            string -> {
                try {
                    return (new BlockArgumentParser(new StringReader(string), false)).parse(false).getBlockState();
                } catch (CommandSyntaxException e) {
                    throw new JsonParseException(e);
                }
            });

    public static final SerializableDataType<ParticleEffect> PARTICLE_EFFECT = SerializableDataType.compound(ParticleEffect.class,
            new SerializableData()
                    .add("type", PARTICLE_TYPE)
                    .add("params", STRING, ""),
            dataInstance -> {
                ParticleType<? extends ParticleEffect> particleType = dataInstance.get("type");
                ParticleEffect.Factory factory = particleType.getParametersFactory();
                ParticleEffect effect = null;
                try {
                    effect = factory.read(particleType, new StringReader(" " + dataInstance.getString("params")));
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
                return effect;
            },
            ((serializableData, particleEffect) -> {
                SerializableData.Instance data = serializableData.new Instance();
                data.set("type", particleEffect.getType());
                String params = particleEffect.asString();
                int spaceIndex = params.indexOf(' ');
                if(spaceIndex > -1) {
                    params = params.substring(spaceIndex + 1);
                } else {
                    params = "";
                }
                data.set("params", params);
                return data;
            }));

    public static final SerializableDataType<ParticleEffect> PARTICLE_EFFECT_OR_TYPE = new SerializableDataType<>(ParticleEffect.class,
            PARTICLE_EFFECT::send,
            PARTICLE_EFFECT::receive,
            jsonElement -> {
                if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                    ParticleType<?> type = PARTICLE_TYPE.read(jsonElement);
                    if(type instanceof ParticleEffect) {
                        return (ParticleEffect) type;
                    }
                    throw new RuntimeException("Expected either a string with a parameter-less particle effect, or an object.");
                } else if(jsonElement.isJsonObject()) {
                    return PARTICLE_EFFECT.read(jsonElement);
                }
                throw new RuntimeException("Expected either a string with a parameter-less particle effect, or an object.");
            });

    public static final SerializableDataType<Vec3d> VECTOR = new SerializableDataType<>(Vec3d.class,
            (packetByteBuf, vector3d) -> {
                packetByteBuf.writeDouble(vector3d.x);
                packetByteBuf.writeDouble(vector3d.y);
                packetByteBuf.writeDouble(vector3d.z);
            },
            (packetByteBuf -> new Vec3d(
                    packetByteBuf.readDouble(),
                    packetByteBuf.readDouble(),
                    packetByteBuf.readDouble())),
            (jsonElement -> {
                if(jsonElement.isJsonObject()) {
                    JsonObject jo = jsonElement.getAsJsonObject();
                    return new Vec3d(
                            JsonHelper.getFloat(jo, "x", 0),
                            JsonHelper.getFloat(jo, "y", 0),
                            JsonHelper.getFloat(jo, "z", 0)
                    );
                } else {
                    throw new JsonParseException("Expected an object with x, y, and z fields.");
                }
            }));

    public static final SerializableDataType<StatusEffectChance> STATUS_EFFECT_CHANCE =
            SerializableDataType.compound(StatusEffectChance.class, new SerializableData()
                            .add("effect", SerializableDataType.STATUS_EFFECT_INSTANCE)
                            .add("chance", SerializableDataType.FLOAT, 1.0F),
                    (data) -> {
                        StatusEffectChance sec = new StatusEffectChance();
                        sec.statusEffectInstance = data.get("effect");
                        sec.chance = data.getFloat("chance");
                        return sec;
                    },
                    (data, csei) -> {
                        SerializableData.Instance inst = data.new Instance();
                        inst.set("effect", csei.statusEffectInstance);
                        inst.set("chance", csei.chance);
                        return inst;
                    });

    public static final SerializableDataType<List<StatusEffectChance>> STATUS_EFFECT_CHANCES = SerializableDataType.list(STATUS_EFFECT_CHANCE);

    public static final SerializableDataType<FoodComponent> FOOD_COMPONENT = SerializableDataType.compound(FoodComponent.class, new SerializableData()
                    .add("hunger", SerializableDataType.INT)
                    .add("saturation", SerializableDataType.FLOAT)
                    .add("meat", SerializableDataType.BOOLEAN, false)
                    .add("always_edible", SerializableDataType.BOOLEAN, false)
                    .add("snack", SerializableDataType.BOOLEAN, false)
                    .add("effect", STATUS_EFFECT_CHANCE, null)
                    .add("effects", STATUS_EFFECT_CHANCES, null),
            (data) -> {
                FoodComponent.Builder builder = new FoodComponent.Builder().hunger(data.getInt("hunger")).saturationModifier(data.getFloat("saturation"));
                if (data.getBoolean("meat")) {
                    builder.meat();
                }
                if (data.getBoolean("always_edible")) {
                    builder.alwaysEdible();
                }
                if (data.getBoolean("snack")) {
                    builder.snack();
                }
                if (data.isPresent("effect")) {
                    StatusEffectChance sec = (StatusEffectChance)data.get("effect");
                    builder.statusEffect(sec.statusEffectInstance, sec.chance);
                }
                if (data.isPresent("effects")) {
                    List<StatusEffectChance> secs = (List<StatusEffectChance>)data.get("effects");
                    secs.forEach(sec -> builder.statusEffect(sec.statusEffectInstance, sec.chance));
                }
                return builder.build();
            },
            (data, fc) -> {
                SerializableData.Instance inst = data.new Instance();
                inst.set("hunger", fc.getHunger());
                inst.set("saturation", fc.getSaturationModifier());
                inst.set("meat", fc.isMeat());
                inst.set("always_edible", fc.isAlwaysEdible());
                inst.set("snack", fc.isSnack());
                inst.set("effect", null);
                List<StatusEffectChance> statusEffectChances = new LinkedList<>();
                fc.getStatusEffects().forEach(pair -> {
                    StatusEffectChance sec = new StatusEffectChance();
                    sec.statusEffectInstance = pair.getFirst();
                    sec.chance = pair.getSecond();
                    statusEffectChances.add(sec);
                });
                if(statusEffectChances.size() > 0) {
                    inst.set("effects", statusEffectChances);
                } else {
                    inst.set("effects", null);
                }
                return inst;
            });

    public static final SerializableDataType<Hand> HAND = SerializableDataType.enumValue(Hand.class);
}
