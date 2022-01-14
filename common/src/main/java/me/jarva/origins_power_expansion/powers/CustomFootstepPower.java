package me.jarva.origins_power_expansion.powers;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CustomFootstepPower extends Power {
    private SoundEvent footstepSound;
    private float pitch;
    private float volume;

    public CustomFootstepPower(PowerType<?> type, Player player, SoundEvent footstepSound, float volume, float pitch){
        super(type, player);
        this.footstepSound = footstepSound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public void playFootstep(Entity entity) {
        entity.playSound(this.footstepSound, this.volume, this.pitch);
    }
}