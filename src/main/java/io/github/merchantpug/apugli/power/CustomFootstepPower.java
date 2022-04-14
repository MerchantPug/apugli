package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.util.ApugliDataTypes;
import io.github.merchantpug.apugli.util.SoundEventPitchVolume;
import io.github.merchantpug.apugli.util.SoundEventWeight;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class CustomFootstepPower extends Power {
    private final List<SoundEventWeight> sounds = new ArrayList<>();
    private final Boolean muted;
    private final float pitch;
    private final float volume;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<CustomFootstepPower>(
            Apugli.identifier("custom_footstep"),
            new SerializableData()
                .add("muted", SerializableDataTypes.BOOLEAN, false)
                .add("sound", ApugliDataTypes.SOUND_EVENT_OPTIONAL_WEIGHT, null)
                .add("sounds", SerializableDataType.list(ApugliDataTypes.SOUND_EVENT_OPTIONAL_WEIGHT), null)
                .add("volume", SerializableDataTypes.FLOAT, 1F)
                .add("pitch", SerializableDataTypes.FLOAT, 1F),
            data ->
                    (type, entity) -> {
                CustomFootstepPower power = new CustomFootstepPower(type, entity, data.getBoolean("muted"), data.getFloat("volume"), data.getFloat("pitch"));
                if (data.isPresent("sound")) {
                    power.addSound(data.get("sound"));
                }
                if (data.isPresent("sounds")) {
                    ((List<SoundEventWeight>)data.get("sounds")).forEach(power::addSound);
                }
                return power;
            })
            .allowCondition();
    }

    public CustomFootstepPower(PowerType<?> type, LivingEntity living, Boolean muted, float volume, float pitch){
        super(type, living);
        this.muted = muted;
        this.pitch = pitch;
        this.volume = volume;
    }

    public void addSound(SoundEventWeight sew) {
        this.sounds.add(sew);
    }

    public Boolean isMuted() {
        return muted;
    }

    public void playFootstep(Entity entity) {
        if (this.muted) return;
        int totalWeight = 0;
        for (SoundEventWeight sew : sounds) {
            totalWeight += sew.weight;
        }

        int index = 0;
        for (double r = Math.random() * totalWeight; index < sounds.size() - 1; ++index) {
            r -= sounds.get(index).weight;
            if (r <= 0.0) break;
        }

        sounds.get(index).soundEventList.forEach(soundEventPitchVolume -> {
            float vol = Float.isNaN(soundEventPitchVolume.volume) ? this.volume : soundEventPitchVolume.volume;
            float pit = Float.isNaN(soundEventPitchVolume.pitch) ? this.pitch : soundEventPitchVolume.pitch;
            entity.playSound(soundEventPitchVolume.soundEvent, vol, pit);
        });
    }
}