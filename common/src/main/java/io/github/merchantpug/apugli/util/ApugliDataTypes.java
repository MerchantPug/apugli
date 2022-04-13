package io.github.merchantpug.apugli.util;

import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ApugliDataTypes {

    public static final SerializableDataType<PlayerModelType> PLAYER_MODEL_TYPE =
            SerializableDataType.enumValue(PlayerModelType.class);

    public static final SerializableDataType<SoundEventPitchVolume> SOUND_EVENT_PITCH_VOLUME =
            SerializableDataType.compound(SoundEventPitchVolume.class, new SerializableData()
                            .add("sound", SerializableDataType.SOUND_EVENT)
                            .add("pitch", SerializableDataType.FLOAT, Float.NaN)
                            .add("volume", SerializableDataType.FLOAT, Float.NaN),
                    (data) -> {
                        SoundEventPitchVolume sepv = new SoundEventPitchVolume();
                        sepv.soundEvent = data.get("sound");
                        sepv.pitch = data.getFloat("pitch");
                        sepv.volume = data.getFloat("volume");
                        return sepv;
                    },
                    (data, sepv) -> {
                        SerializableData.Instance inst = data.new Instance();
                        inst.set("sound", sepv.soundEvent);
                        inst.set("pitch", sepv.pitch);
                        inst.set("volume", sepv.volume);
                        return inst;
                    });

    public static final SerializableDataType<SoundEventPitchVolume> SOUND_EVENT_OPTIONAL_PITCH_VOLUME = new SerializableDataType<>(SoundEventPitchVolume.class,
            SOUND_EVENT_PITCH_VOLUME::send,
            SOUND_EVENT_PITCH_VOLUME::receive,
            jsonElement -> {
                if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                    SoundEventPitchVolume sepv = new SoundEventPitchVolume();
                    sepv.soundEvent = SerializableDataType.SOUND_EVENT.read(jsonElement);
                    sepv.pitch = Float.NaN;
                    sepv.volume = Float.NaN;
                    if(sepv.soundEvent != null) {
                        return sepv;
                    }
                    throw new RuntimeException("Expected either a string with a parameter-less sound event, or an object.");
                } else if (jsonElement.isJsonObject()) {
                    return SOUND_EVENT_PITCH_VOLUME.read(jsonElement);
                }
                throw new RuntimeException("Expected either a string with a parameter-less sound event, or an object.");
            });

    public static final SerializableDataType<SoundEventWeight> SOUND_EVENT_WEIGHT =
            SerializableDataType.compound(SoundEventWeight.class, new SerializableData()
                            .add("sound", SOUND_EVENT_OPTIONAL_PITCH_VOLUME, null)
                            .add("sounds", SerializableDataType.list(SOUND_EVENT_OPTIONAL_PITCH_VOLUME), null)
                            .add("weight", SerializableDataType.INT, 1),
                    (data) -> {
                        SoundEventWeight sewi = new SoundEventWeight();
                        List<SoundEventPitchVolume> soundEventPitchVolumes = new ArrayList<>();
                        if (data.isPresent("sound")) {
                            soundEventPitchVolumes.add(data.get("sound"));
                        }
                        if (data.isPresent("sounds")) {
                            soundEventPitchVolumes.addAll(data.get("sounds"));
                        }
                        sewi.soundEventList = soundEventPitchVolumes;
                        sewi.weight = data.getInt("weight");
                        return sewi;
                    },
                    (data, sewi) -> {
                        SerializableData.Instance inst = data.new Instance();
                        inst.set("sound", null);
                        if(sewi.soundEventList.size() > 0) {
                            inst.set("sounds", sewi.soundEventList);
                        } else {
                            inst.set("sounds", null);
                        }
                        inst.set("weight", sewi.weight);
                        return inst;
                    });

    public static final SerializableDataType<SoundEventWeight> SOUND_EVENT_OPTIONAL_WEIGHT = new SerializableDataType<>(SoundEventWeight.class,
            SOUND_EVENT_WEIGHT::send,
            SOUND_EVENT_WEIGHT::receive,
            jsonElement -> {
                if(jsonElement.isJsonPrimitive()) {
                    SoundEventWeight sewi = new SoundEventWeight();
                    SoundEventPitchVolume sepv = new SoundEventPitchVolume();
                    sepv.soundEvent = SerializableDataType.SOUND_EVENT.read(jsonElement);
                    sepv.pitch = Float.NaN;
                    sepv.volume = Float.NaN;
                    sewi.soundEventList.add(sepv);
                    sewi.weight = 1;
                    if(sepv.soundEvent != null) {
                        return sewi;
                    }
                }
                if (!jsonElement.isJsonObject()) {
                    throw new RuntimeException("Expected either a string with a parameter-less sound event, or an object.");
                }
                if (jsonElement.getAsJsonObject().has("pitch") || jsonElement.getAsJsonObject().has("volume")) {
                    SoundEventWeight sew = new SoundEventWeight();
                    sew.soundEventList.add(SOUND_EVENT_OPTIONAL_PITCH_VOLUME.read(jsonElement));
                    sew.weight = 1;
                    if(sew.soundEventList.size() > 0) {
                        return sew;
                    }
                } else {
                    return SOUND_EVENT_WEIGHT.read(jsonElement);
                }
                throw new RuntimeException("Expected either a string with a parameter-less sound event, or an object.");
            });
}
