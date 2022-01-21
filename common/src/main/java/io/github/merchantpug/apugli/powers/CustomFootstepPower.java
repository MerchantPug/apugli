package io.github.merchantpug.apugli.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
                .add("muted", SerializableDataType.BOOLEAN, false)
                .add("sound", SerializableDataType.SOUND_EVENT, null)
                .add("volume", SerializableDataType.FLOAT, 1F)
                .add("pitch", SerializableDataType.FLOAT, 1F),
            data ->
                    (type, player) -> {
                return new CustomFootstepPower(type, player, data.getBoolean("muted"), (SoundEvent) data.get("sound"), data.getFloat("volume"), data.getFloat("pitch"));
            })
            .allowCondition();
    }

    public CustomFootstepPower(PowerType<?> type, PlayerEntity player, Boolean muted, SoundEvent footstepSound, float volume, float pitch){
        super(type, player);
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