package io.github.merchantpug.apugli.power;

import io.github.apace100.apoli.power.ActiveCooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.util.HudRender;
import io.github.merchantpug.apugli.networking.packet.LightUpBlockPacket;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class LightUpBlockPower extends ActiveCooldownPower {
    private final int burnTime;
    private final int brewTime;
    private final ParticleType particle;
    private final int particleCount;
    private final SoundEvent soundEvent;

    public LightUpBlockPower(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, int burnTime, int brewTime, ParticleType particle, int particleCount, SoundEvent soundEvent) {
        super(type, entity, cooldownDuration, hudRender, null);
        this.burnTime = burnTime;
        this.brewTime = brewTime;
        this.particle = particle;
        this.particleCount = particleCount;
        this.soundEvent = soundEvent;
    }

    public void onUse() {
        if (this.canUse()) {
            if (entity.world.isClient) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = ((BlockHitResult) client.crosshairTarget).getBlockPos();
                    BlockState state = client.world.getBlockState(pos);
                    if (state.getBlock() instanceof AbstractFurnaceBlock || state.getBlock() instanceof CampfireBlock || state.getBlock() instanceof BrewingStandBlock) {
                        LightUpBlockPacket.send(pos, particle, particleCount, burnTime, brewTime, soundEvent);
                        this.use();
                    }
                }
            }
        }
    }
}
