package io.github.merchantpug.apugli.util;

import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;

@SuppressWarnings("deprecation")
public class ApugliDataTypes {

    public static final SerializableDataType<PlayerModelType> PLAYER_MODEL_TYPE =
            SerializableDataType.enumValue(PlayerModelType.class);

    public static final SerializableDataType<SoundEventWeight> SOUND_EVENT_WEIGHT =
            SerializableDataType.compound(SoundEventWeight.class, new SerializableData()
                            .add("sound", SerializableDataType.SOUND_EVENT)
                            .add("weight", SerializableDataType.INT, 1),
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
                    sew.soundEvent = SerializableDataType.SOUND_EVENT.read(jsonElement);
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
}
