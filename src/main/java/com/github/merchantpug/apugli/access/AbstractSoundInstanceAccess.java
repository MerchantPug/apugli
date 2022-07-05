package com.github.merchantpug.apugli.access;

import net.minecraft.sound.SoundEvent;

public interface AbstractSoundInstanceAccess {
    void setSoundEvent(SoundEvent soundEvent);
    SoundEvent getSoundEvent();
}
