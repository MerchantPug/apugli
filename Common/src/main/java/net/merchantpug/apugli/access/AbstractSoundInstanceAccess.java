package net.merchantpug.apugli.access;

import net.minecraft.sounds.SoundEvent;

public interface AbstractSoundInstanceAccess {
    void setSoundEvent(SoundEvent soundEvent);
    SoundEvent getSoundEvent();
}
