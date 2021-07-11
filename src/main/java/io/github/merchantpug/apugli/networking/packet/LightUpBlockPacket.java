package io.github.merchantpug.apugli.networking.packet;

import draylar.fabricfurnaces.block.FabricFurnaceBlock;
import draylar.fabricfurnaces.entity.FabricFurnaceEntity;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.mixin.AbstractFurnaceBlockEntityAccessor;
import io.github.merchantpug.apugli.mixin.BrewingStandBlockEntityAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import static net.minecraft.state.property.Properties.LIT;

public class LightUpBlockPacket {
    public static final Identifier ID = Apugli.identifier("light_up_block");

    public static void send(BlockPos pos, ParticleType particle, int particleCount, int burnTime, int brewTime, SoundEvent soundEvent) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ParticleEffect particleEffect = (ParticleEffect)particle;
        buf.writeBlockPos(pos);
        buf.writeInt(Registry.PARTICLE_TYPE.getRawId(particleEffect.getType()));
        buf.writeInt(particleCount);
        buf.writeInt(burnTime);
        buf.writeInt(brewTime);
        buf.writeInt(Registry.SOUND_EVENT.getRawId(soundEvent));
        particleEffect.write(buf);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        BlockPos pos = buf.readBlockPos();
        ParticleType<?> particle = Registry.PARTICLE_TYPE.get(buf.readInt());
        int particleCount = buf.readInt();
        int burnTime = buf.readInt();
        int brewTime = buf.readInt();
        SoundEvent soundEvent = Registry.SOUND_EVENT.get(buf.readInt());
        ParticleEffect particleEffect = readParticleParameters(buf, particle);
        server.execute(() -> {
            BlockState state = player.world.getBlockState(pos);
            BlockEntity entity = player.world.getBlockEntity(pos);
            if (state.getBlock() instanceof AbstractFurnaceBlock || state.getBlock() instanceof CampfireBlock) {
                player.world.setBlockState(pos, state.with(LIT, true).with(LIT, true), 2);
                spawnParticles(particleEffect, pos, particleCount, player);
                player.swingHand(Hand.MAIN_HAND, true);
                playSound(player, soundEvent);
                player.world.syncWorldEvent(1590, pos, 0);
            }
            if (entity instanceof AbstractFurnaceBlockEntity) {
                if (((AbstractFurnaceBlockEntityAccessor)entity).getBurnTime() < burnTime) {
                    ((AbstractFurnaceBlockEntityAccessor)entity).setFuelTime(burnTime);
                    ((AbstractFurnaceBlockEntityAccessor)entity).setBurnTime(burnTime);
                    player.world.syncWorldEvent(1591, pos, 0);
                }
            }
            if (entity instanceof BrewingStandBlockEntity) {
                if (((BrewingStandBlockEntityAccessor)entity).getFuel() < brewTime) {
                    ((BrewingStandBlockEntityAccessor) entity).setFuel(brewTime);
                }
                spawnParticles(particleEffect, pos, particleCount, player);
                player.swingHand(Hand.MAIN_HAND, true);
                playSound(player, soundEvent);
                player.world.syncWorldEvent(1592, pos, 0);
            }

            if (FabricLoader.getInstance().isModLoaded("fabric-furnaces")) {
                if (state.getBlock() instanceof FabricFurnaceBlock) {
                    player.world.setBlockState(pos, state.with(LIT, true).with(LIT, true), 2);
                    spawnParticles(particleEffect, pos, particleCount, player);
                    player.swingHand(Hand.MAIN_HAND, true);
                    playSound(player, soundEvent);
                    player.world.syncWorldEvent(1590, pos, 0);
                }
            }
        });
    }

    private static void playSound(LivingEntity player, SoundEvent soundEvent) {
        if (soundEvent != null) {
            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), soundEvent, SoundCategory.NEUTRAL, 0.5F, (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.2F + 1.0F);
        }
    }

    private static void spawnParticles(ParticleEffect particleEffect, BlockPos pos, int particleCount, LivingEntity player) {
        if (particleEffect != null && particleCount > 0) {
            ((ServerWorld)player.world).spawnParticles(particleEffect, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, particleCount, player.getRandom().nextDouble() * 0.2D - 0.1D, 0.1D, player.getRandom().nextDouble() * 0.2D - 0.1D, 0.05D);
        }
    }

    private static <T extends ParticleEffect> T readParticleParameters(PacketByteBuf buf, ParticleType<T> type) {
        return type.getParametersFactory().read(type, buf);
    }
}
