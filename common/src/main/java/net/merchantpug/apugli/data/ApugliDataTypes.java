package net.merchantpug.apugli.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.util.Comparison;
import io.github.apace100.calio.ClassUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            },
            sepv -> {
                if (Float.isNaN(sepv.pitch) && Float.isNaN(sepv.volume)) {
                    return SerializableDataTypes.SOUND_EVENT.write(sepv.soundEvent);
                }
                return SOUND_EVENT_PITCH_VOLUME.write(sepv);
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
                        if(!sewi.soundEventList.isEmpty()) {
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
                    if(!sew.soundEventList.isEmpty()) {
                        return sew;
                    }
                } else {
                    return SOUND_EVENT_WEIGHT.read(jsonElement);
                }
                throw new JsonParseException("Expected either a string with a parameter-less sound event, or an object.");
            },
            sew -> {
                if (sew.soundEventList.size() == 1 && Float.isNaN(sew.soundEventList.get(0).pitch) && Float.isNaN(sew.soundEventList.get(0).volume))
                    return SerializableDataTypes.SOUND_EVENT.write(sew.soundEventList.get(0).soundEvent);
                return SOUND_EVENT_WEIGHT.write(sew);
            });

    public static <V> SerializableDataType<Map<Comparison, V>> comparisonMap(SerializableDataType<V> valueDataType) {
        return map("comparison", ApoliDataTypes.COMPARISON, "compare_to", valueDataType);
    }

    public static <K, V> SerializableDataType<Map<K, V>> map(String keyFieldName, SerializableDataType<K> keyDataType, String valueFieldName, SerializableDataType<V> valueDataType) {
        return new SerializableDataType<>(ClassUtil.castClass(Map.class), (buf, map) -> {
            buf.writeInt(map.size());
            map.forEach((k, v) -> {
                keyDataType.send(buf, k);
                valueDataType.send(buf, v);
            });
        }, buf -> {
            Map<K, V> map = new HashMap<>();
            int mapSize = buf.readInt();
            for (int i = 0; i < mapSize; ++i) {
                map.put(keyDataType.receive(buf), valueDataType.receive(buf));
            }
            return map;
        }, json -> {
            Map<K, V> map = new HashMap<>();
            if (json.isJsonArray()) {
                for (int i = 0; i < json.getAsJsonArray().size(); ++i) {
                    JsonElement jsonElement = json.getAsJsonArray().get(i);
                    if (jsonElement.isJsonObject()) {
                        map.put(keyDataType.read(jsonElement.getAsJsonObject().get(keyFieldName)), valueDataType.read(jsonElement.getAsJsonObject().get(valueFieldName)));
                    } else {
                        throw new JsonParseException("Expected an object inside the map array at index: " + i);
                    }
                }
                return map;
            } else if (json.isJsonObject()) {
                map.put(keyDataType.read(json.getAsJsonObject().get(keyFieldName)), valueDataType.read(json.getAsJsonObject().get(valueFieldName)));
                return map;
            }
            throw new JsonParseException("Expected either an array or an object.");
        },
        kvMap -> {
            JsonArray jsonArray = new JsonArray();
            kvMap.forEach((k, v) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.add(keyFieldName, keyDataType.write(k));
                jsonObject.add(keyFieldName, valueDataType.write(v));
                jsonArray.add(jsonObject);
            });
            return jsonArray;
        });
    }

}
