package io.github.merchantpug.apugli.util;

import com.google.gson.JsonObject;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.util.StatusEffectChance;
import io.github.merchantpug.apugli.behavior.BehaviorFactory;
import io.github.merchantpug.apugli.behavior.MobBehavior;
import io.github.merchantpug.apugli.registry.ApugliRegistries;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import oshi.util.tuples.Pair;

import java.util.List;

public class ApugliDataTypes {
    public static final SerializableDataType<PlayerModelType> PLAYER_MODEL_TYPE =
            SerializableDataType.enumValue(PlayerModelType.class);

    public static final SerializableDataType<SoundEventWeight> SOUND_EVENT_WEIGHT =
            SerializableDataType.compound(SoundEventWeight.class, new SerializableData()
                            .add("sound", SerializableDataTypes.SOUND_EVENT)
                            .add("weight", SerializableDataTypes.INT, 1),
                    (data) -> {
                        SoundEventWeight sec = new SoundEventWeight();
                        sec.soundEvent = data.get("sound");
                        sec.weight = data.getInt("weight");
                        return sec;
                    },
                    (data, sewi) -> {
                        SerializableData.Instance inst = data.new Instance();
                        inst.set("sound", sewi.soundEvent);
                        inst.set("weight", sewi.weight);
                        return inst;
                    });

    public static final SerializableDataType<SoundEventWeight> SOUND_EVENT_OPTIONAL_WEIGHT = new SerializableDataType<>(SoundEventWeight.class,
            SOUND_EVENT_WEIGHT::send,
            SOUND_EVENT_WEIGHT::receive,
            jsonElement -> {
                if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                    SoundEventWeight sew = new SoundEventWeight();
                    sew.soundEvent = SerializableDataTypes.SOUND_EVENT.read(jsonElement);
                    sew.weight = 1;
                    if(sew.soundEvent != null) {
                        return sew;
                    }
                    throw new RuntimeException("Expected either a string with a parameter-less sound event, or an object.");
                } else if(jsonElement.isJsonObject()) {
                    return SOUND_EVENT_WEIGHT.read(jsonElement);
                }
                throw new RuntimeException("Expected either a string with a parameter-less sound event, or an object.");
            });

    public static final SerializableDataType<MobBehavior> MOB_BEHAVIOR = new SerializableDataType<>(MobBehavior.class, (buffer, mobBehaviour) -> mobBehaviour.send(buffer),
            (buffer) -> {
                Identifier type = buffer.readIdentifier();
                BehaviorFactory factory = ApugliRegistries.BEHAVIOR_FACTORIES.get(type);
                return factory.read(buffer);
            },
            (jsonElement) -> {
                if(jsonElement.isJsonObject()) {
                    JsonObject jo = jsonElement.getAsJsonObject();
                    String type = JsonHelper.getString(jo, "type");
                    try {
                        BehaviorFactory factory = ApugliRegistries.BEHAVIOR_FACTORIES.get(new Identifier(type));
                        return factory.read(jo);
                    } catch (NullPointerException e) {
                        BehaviorFactory.throwExceptionMessages(jo);
                    }
                }
                return null;
            });
}
