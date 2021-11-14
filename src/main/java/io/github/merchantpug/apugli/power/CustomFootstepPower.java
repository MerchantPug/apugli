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

public class CustomFootstepPower extends Power {
    private final SoundEvent footstepSound;
    private final Boolean muted;
    private final float pitch;
    private final float volume;

    public static PowerFactory<?> getFactory() {
        return new PowerFactory<CustomFootstepPower>(
            Apugli.identifier("custom_footstep"),
            new SerializableData()
                .add("muted", SerializableDataTypes.BOOLEAN, false)
                .add("sound", SerializableDataTypes.SOUND_EVENT, null)
                .add("volume", SerializableDataTypes.FLOAT, 1F)
                .add("pitch", SerializableDataTypes.FLOAT, 1F),
            data ->
                    (type, player) -> {
                return new CustomFootstepPower(type, player, data.getBoolean("muted"), (SoundEvent) data.get("sound"), data.getFloat("volume"), data.getFloat("pitch"));
            })
            .allowCondition();
    }

    public CustomFootstepPower(PowerType<?> type, LivingEntity living, Boolean muted, SoundEvent footstepSound, float volume, float pitch){
        super(type, living);
        this.muted = muted;
        this.footstepSound = footstepSound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public Boolean isMuted() {
        return muted;
    }

    public void playFootstep(Entity entity) {
        if (!this.muted) entity.playSound(this.footstepSound, this.volume, this.pitch);
    }
}