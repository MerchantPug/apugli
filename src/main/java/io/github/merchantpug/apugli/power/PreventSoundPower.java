package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class PreventSoundPower extends Power {
    private final List<SoundCategory> categories = new ArrayList<>();
    private final List<SoundEvent> sounds = new ArrayList<>();
    private final List<SoundEvent> soundWhitelist = new ArrayList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<PreventSoundPower>(Apugli.identifier("prevent_sound"),
                new SerializableData()
                        .add("category", SerializableDataType.enumValue(SoundCategory.class), null)
                        .add("categories", SerializableDataType.list(SerializableDataType.enumValue(SoundCategory.class)), null)
                        .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                        .add("sounds", SerializableDataType.list(SerializableDataTypes.SOUND_EVENT), null)
                        .add("whitelist", SerializableDataType.list(SerializableDataTypes.SOUND_EVENT), null),
                data ->
                        (type, entity) -> {
                            PreventSoundPower power = new PreventSoundPower(type, entity);
                            if(data.isPresent("category")) {
                                power.addCategory((SoundCategory)data.get("category"));
                            }
                            if(data.isPresent("categories")) {
                                ((List<SoundCategory>)data.get("categories")).forEach(power::addCategory);
                            }
                            if(data.isPresent("sound")) {
                                power.addSound((SoundEvent)data.get("sound"));
                            }
                            if(data.isPresent("sounds")) {
                                ((List<SoundEvent>)data.get("sounds")).forEach(power::addSound);
                            }
                            if(data.isPresent("whitelist")) {
                                ((List<SoundEvent>)data.get("whitelist")).forEach(power::addWhitelistedSound);
                            }
                            return power;
                        })
                .allowCondition();
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

    public boolean doesApplyToCategory(SoundCategory category) {
        return categories.contains(category);
    }

    public void addCategory(SoundCategory category) {
        this.categories.add(category);
    }

    public PreventSoundPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
}
