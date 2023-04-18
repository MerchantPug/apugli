package net.merchantpug.apugli.power;

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/power/PreventSoundPower.java
import net.merchantpug.apugli.Apugli;
========
import the.great.migration.merchantpug.apugli.Apugli;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/power/PreventSoundPower.java
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class PreventSoundPower extends Power {
    private final List<SoundSource> categories = new ArrayList<>();
    private final List<SoundEvent> sounds = new ArrayList<>();
    private final List<SoundEvent> soundWhitelist = new ArrayList<>();

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<PreventSoundPower>(Apugli.identifier("prevent_sound"),
                new SerializableData()
                        .add("category", SerializableDataType.enumValue(SoundSource.class), null)
                        .add("categories", SerializableDataType.list(SerializableDataType.enumValue(SoundSource.class)), null)
                        .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                        .add("sounds", SerializableDataType.list(SerializableDataTypes.SOUND_EVENT), null)
                        .add("whitelist", SerializableDataType.list(SerializableDataTypes.SOUND_EVENT), null),
                data ->
                        (type, entity) -> {
                            PreventSoundPower power = new PreventSoundPower(type, entity);
                            if(data.isPresent("category")) {
                                power.addCategory(data.get("category"));
                            }
                            if(data.isPresent("categories")) {
                                ((List<SoundSource>)data.get("categories")).forEach(power::addCategory);
                            }
                            if(data.isPresent("sound")) {
                                power.addSound(data.get("sound"));
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

    public boolean doesApplyToCategory(SoundSource category) {
        return categories.contains(category);
    }

    public void addCategory(SoundSource category) {
        this.categories.add(category);
    }

    public PreventSoundPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }
}
