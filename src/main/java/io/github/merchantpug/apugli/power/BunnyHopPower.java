package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.ActiveCooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.merchantpug.apugli.access.LivingEntityAccess;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

public class BunnyHopPower extends ActiveCooldownPower {
    private Key key;
    public final double increasePerTick;
    private final double abilityVelocity;
    public final double maxVelocity;
    private final SoundEvent soundEvent;
    public final int tickRate;

    public BunnyHopPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, double increasePerTick, double abilityVelocity, double maxVelocity, SoundEvent soundEvent, int tickRate) {
        super(type, entity, cooldownDuration, hudRender, null);
        this.increasePerTick = increasePerTick;
        this.abilityVelocity = abilityVelocity;
        this.maxVelocity = maxVelocity;
        this.soundEvent = soundEvent;
        this.tickRate = tickRate;
    }

    @Override
    public void onUse() {
        if (canUse()) {
            if (((LivingEntityAccess)entity).getApugliVelocityMultiplier() < this.maxVelocity / this.increasePerTick) {
                ((LivingEntityAccess)entity).addVelocityMultiplier(this.abilityVelocity);
                if (soundEvent != null) {
                    entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, SoundCategory.PLAYERS, 1.0F, (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F);
                }
                float f = MathHelper.sin(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);
                float h = -MathHelper.cos(entity.getYaw() * 0.017453292F) * MathHelper.cos(entity.getPitch() * 0.017453292F);
                for (int i = 0; i < 15; i++) {
                    entity.world.addParticle(ParticleTypes.CLOUD, (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + entity.getX(), entity.getY(), (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + entity.getZ(), f * 0.25, 0.0, h * 0.25);
                }
                this.use();
            }
        }
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }
}
