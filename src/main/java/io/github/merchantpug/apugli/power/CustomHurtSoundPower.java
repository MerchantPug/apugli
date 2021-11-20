package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;

public class CustomHurtSoundPower extends Power {
    private final SoundEvent hurtSound;
    private final Boolean muted;
    private final float pitch;
    private final float volume;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<CustomHurtSoundPower>(
            Apugli.identifier("custom_hurt_sound"),
            new SerializableData()
                .add("muted", SerializableDataTypes.BOOLEAN, false)
                .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                .add("volume", SerializableDataTypes.FLOAT, 1F)
                .add("pitch", SerializableDataTypes.FLOAT, 1F),
            data ->
                    (type, player) -> {
                return new CustomHurtSoundPower(type, player, data.getBoolean("muted"), (SoundEvent) data.get("sound"), data.getFloat("volume"), data.getFloat("pitch"));
            })
            .allowCondition();
    }

    public CustomHurtSoundPower(PowerType<?> type, LivingEntity living, Boolean muted, SoundEvent hurtSound, float volume, float pitch){
        super(type, living);
        this.muted = muted;
        this.hurtSound = hurtSound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public Boolean isMuted() {
        return muted;
    }

    public void playDeathSound(Entity entity) {
        if (!this.muted) entity.playSound(this.hurtSound, this.volume, this.pitch);
    }
}