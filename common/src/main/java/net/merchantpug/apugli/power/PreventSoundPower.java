package net.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.power.factory.SimplePowerFactory;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class PreventSoundPower extends Power {
    private final List<SoundSource> categories = new ArrayList<>();
    private final List<SoundEvent> sounds = new ArrayList<>();
    private final List<SoundEvent> soundWhitelist = new ArrayList<>();

    public PreventSoundPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public boolean isSoundNotWhitelisted(SoundEvent soundEvent) {
        return !soundWhitelist.contains(soundEvent);
    }

    public void addWhitelistedSound(SoundEvent soundEvent) {
        this.soundWhitelist.add(soundEvent);
    }

    public boolean doesApplyToSound(SoundEvent soundEvent) {
        return sounds.contains(soundEvent);
    }

    public void addSound(SoundEvent soundEvent) {
        this.sounds.add(soundEvent);
    }

    public boolean doesApplyToCategory(SoundSource category) {
        return categories.contains(category);
    }

    public void addCategory(SoundSource category) {
        this.categories.add(category);
    }

    public static class Factory extends SimplePowerFactory<PreventSoundPower> {

        public Factory() {
            super("prevent_sound",
                    new SerializableData()
                            .add("category", SerializableDataType.enumValue(SoundSource.class), null)
                            .add("categories", SerializableDataType.list(SerializableDataType.enumValue(SoundSource.class)), null)
                            .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                            .add("sounds", SerializableDataType.list(SerializableDataTypes.SOUND_EVENT), null)
                            .add("whitelist", SerializableDataType.list(SerializableDataTypes.SOUND_EVENT), null),
                    data -> (type, entity) -> {
                        PreventSoundPower power = new PreventSoundPower(type, entity);
                        data.ifPresent("category", power::addCategory);
                        data.<List<SoundSource>>ifPresent("categories", categories -> categories.forEach(power::addCategory));
                        data.ifPresent("sound", power::addSound);
                        data.<List<SoundEvent>>ifPresent("sound", soundEvents -> soundEvents.forEach(power::addSound));
                        data.<List<SoundEvent>>ifPresent("whitelist", soundEvents -> soundEvents.forEach(power::addWhitelistedSound));
                        return power;
                    });
            allowCondition();
        }

        @Override
        public Class<PreventSoundPower> getPowerClass() {
            return PreventSoundPower.class;
        }

    }
}
