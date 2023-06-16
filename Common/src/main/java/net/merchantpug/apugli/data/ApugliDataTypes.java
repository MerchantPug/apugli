package net.merchantpug.apugli.data;

import com.google.gson.JsonParseException;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.util.PlayerModelType;
import net.merchantpug.apugli.util.SoundEventPitchVolume;
import net.merchantpug.apugli.util.SoundEventWeight;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public class ApugliDataTypes {
    
    public static final SerializableDataType<PlayerModelType> PLAYER_MODEL_TYPE =
            SerializableDataType.enumValue(PlayerModelType.class);

    public static final SerializableDataType<TagKey<Structure>> STRUCTURE_TAG = SerializableDataType.tag(Registries.STRUCTURE);

    public static final SerializableDataType<ResourceKey<Structure>> STRUCTURE = SerializableDataType.registryKey(Registries.STRUCTURE);

    public static final SerializableDataType<SoundEventPitchVolume> SOUND_EVENT_PITCH_VOLUME =
            SerializableDataType.compound(SoundEventPitchVolume.class, new SerializableData()
                            .add("sound", SerializableDataTypes.SOUND_EVENT)
                            .add("pitch", SerializableDataTypes.FLOAT, Float.NaN)
                            .add("volume", SerializableDataTypes.FLOAT, Float.NaN),
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
                    sepv.soundEvent = SerializableDataTypes.SOUND_EVENT.read(jsonElement);
                    sepv.pitch = Float.NaN;
                    sepv.volume = Float.NaN;
                    if(sepv.soundEvent != null) {
                        return sepv;
                    }
                    throw new RuntimeException("Expected either a string with a parameter-less sound event, or an object.");
                } else if(jsonElement.isJsonObject()) {
                    return SOUND_EVENT_PITCH_VOLUME.read(jsonElement);
                }
                throw new RuntimeException("Expected either a string with a parameter-less sound event, or an object.");
            });

    public static final SerializableDataType<SoundEventWeight> SOUND_EVENT_WEIGHT =
            SerializableDataType.compound(SoundEventWeight.class, new SerializableData()
                            .add("sound", SOUND_EVENT_OPTIONAL_PITCH_VOLUME, null)
                            .add("sounds", SerializableDataType.list(SOUND_EVENT_OPTIONAL_PITCH_VOLUME), null)
                            .add("weight", SerializableDataTypes.INT, 1),
                    (data) -> {
                        SoundEventWeight sewi = new SoundEventWeight();
                        List<SoundEventPitchVolume> soundEventPitchVolumes = new ArrayList<>();
                        data.<SoundEventPitchVolume>ifPresent("sound", soundEventPitchVolumes::add);
                        data.<List<SoundEventPitchVolume>>ifPresent("sounds", soundEventPitchVolumes::addAll);
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
                    sepv.soundEvent = SerializableDataTypes.SOUND_EVENT.read(jsonElement);
                    sepv.pitch = Float.NaN;
                    sepv.volume = Float.NaN;
                    sewi.soundEventList.add(sepv);
                    sewi.weight = 1;
                    if(sepv.soundEvent != null) {
                        return sewi;
                    }
                }
                if(!jsonElement.isJsonObject()) {
                    throw new JsonParseException("Expected either a string with a parameter-less sound event, or an object.");
                }
                if(jsonElement.getAsJsonObject().get("pitch") != null || jsonElement.getAsJsonObject().get("volume") != null) {
                    SoundEventWeight sew = new SoundEventWeight();
                    sew.soundEventList.add(SOUND_EVENT_OPTIONAL_PITCH_VOLUME.read(jsonElement));
                    sew.weight = 1;
                    if(sew.soundEventList.size() > 0) {
                        return sew;
                    }
                } else {
                    return SOUND_EVENT_WEIGHT.read(jsonElement);
                }
                throw new JsonParseException("Expected either a string with a parameter-less sound event, or an object.");
            });
    
}
