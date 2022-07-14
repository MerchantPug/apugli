package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.player.PlayerEntity;
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
                        .add("sound", SerializableDataType.SOUND_EVENT, null)
                        .add("sounds", SerializableDataType.list(SerializableDataType.SOUND_EVENT), null)
                        .add("whitelist", SerializableDataType.list(SerializableDataType.SOUND_EVENT), null),
                data ->
                        (type, player) -> {
                            PreventSoundPower power = new PreventSoundPower(type, player);
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

    public PreventSoundPower(PowerType<?> type, PlayerEntity player) {
        super(type, player);
    }
}
