package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.data.ApugliDataTypes;
import net.merchantpug.apugli.util.SoundEventPitchVolume;
import net.merchantpug.apugli.util.SoundEventWeight;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomSoundPower extends Power {
    private final List<SoundEventWeight> sounds = new ArrayList<>();
    private final boolean muted;
    private final float pitch;
    private final float volume;
    
    public CustomSoundPower(PowerType<?> type, LivingEntity living, boolean muted, float volume, float pitch){
        super(type, living);
        this.muted = muted;
        this.pitch = pitch;
        this.volume = volume;
    }
    
    public CustomSoundPower(PowerType<?> type, LivingEntity living, SerializableData.Instance data) {
        this(type, living, data.getBoolean("muted"), data.getFloat("volume"), data.getFloat("pitch"));
        if(data.isPresent("sound")) sounds.add(data.get("sound"));
        if(data.isPresent("sounds")) sounds.addAll(data.get("sounds"));
    }
    
    public boolean isMuted() {
        return muted;
    }
    
    public void playSound(Entity entity) {
        if(this.muted) return;
        SoundEventWeight chosenSounds = switch(sounds.size()) {
            case 0 -> null;
            case 1 -> sounds.get(0);
            default -> {
                int totalWeight = 0;
                for(SoundEventWeight sew : sounds) {
                    totalWeight += sew.weight;
                }
                int index = 0;
                for(double r = Math.random() * totalWeight; index < sounds.size() - 1; ++index) {
                    r -= sounds.get(index).weight;
                    if(r <= 0.0) break;
                }
                yield sounds.get(index);
            }
        };
        if(chosenSounds == null) return;
        for(SoundEventPitchVolume sound : chosenSounds.soundEventList) {
            float volume = Float.isNaN(sound.volume) ? this.volume : sound.volume;
            float pitch = Float.isNaN(sound.pitch) ? this.pitch : sound.pitch;
            this.playSound(entity, sound.soundEvent, volume, pitch);
        }
    }
    
    protected abstract void playSound(Entity entity, SoundEvent soundEvent, float volume, float pitch);
    
    public static SerializableData getSerializableData() {
        return new SerializableData()
            .add("muted", SerializableDataTypes.BOOLEAN, false)
            .add("sound", ApugliDataTypes.SOUND_EVENT_OPTIONAL_WEIGHT, null)
            .add("sounds", SerializableDataType.list(ApugliDataTypes.SOUND_EVENT_OPTIONAL_WEIGHT), null)
            .add("volume", SerializableDataTypes.FLOAT, 1F)
            .add("pitch", SerializableDataTypes.FLOAT, 1F);
    }
    
}
