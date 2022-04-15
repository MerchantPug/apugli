package io.github.merchantpug.apugli.util;

import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.util.StatusEffectChance;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ApugliDataTypes {
    public static final SerializableDataType<PlayerModelType> PLAYER_MODEL_TYPE =
            SerializableDataType.enumValue(PlayerModelType.class);

    public static final SerializableDataType<TagKey<ConfiguredStructureFeature<?, ?>>> STRUCTURE_TAG = SerializableDataType.tag(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);

    public static final SerializableDataType<RegistryKey<ConfiguredStructureFeature<?, ?>>> STRUCTURE = SerializableDataType.registryKey(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);

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
                } else if (jsonElement.isJsonObject()) {
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
