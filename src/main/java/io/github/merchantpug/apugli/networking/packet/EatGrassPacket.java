package io.github.merchantpug.apugli.networking.packet;

import io.github.merchantpug.apugli.Apugli;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public class EatGrassPacket {
    public static final Identifier ID = Apugli.identifier("eat_grass");

    public static void send(BlockPos pos) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        BlockPos pos = buf.readBlockPos();
        server.execute(() -> {
            if (!player.isBlockBreakingRestricted(player.world, pos, GameMode.ADVENTURE)) {
                BlockState state = player.world.getBlockState(pos);
                if (state.getBlock() instanceof GrassBlock) {
                    player.world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                    player.getHungerManager().add(2, 1.0F);
                    player.world.syncWorldEvent(6386, pos, 0);
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0F, 0.7F);
                    player.swingHand(Hand.MAIN_HAND, true);
                }

                if (state.getBlock() instanceof FernBlock || state.getBlock() instanceof SeagrassBlock) {
                    if (state.getBlock() instanceof SeagrassBlock) {
                        player.world.setBlockState(pos, Blocks.WATER.getDefaultState());
                    } else {
                        player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                    player.getHungerManager().add(5, 6.0F);
                    player.world.syncWorldEvent(6386, pos, 0);
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0F, 0.4F / (player.getRandom().nextFloat() * 0.2F + 0.7F));
                    player.swingHand(Hand.MAIN_HAND, true);
                }

                if (state.getBlock() instanceof TallPlantBlock && !(state.getBlock() instanceof TallFlowerBlock)) {
                    if (state.getBlock() instanceof TallSeagrassBlock) {
                        player.world.setBlockState(pos, Blocks.WATER.getDefaultState());
                    } else {
                        player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                    player.getHungerManager().add(8, 12.8F);
                    player.world.syncWorldEvent(6386, pos, 0);
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0F, 0.4F / (player.getRandom().nextFloat() * 0.2F + 0.7F));
                    player.swingHand(Hand.MAIN_HAND, true);
                }

                if (state.getBlock() instanceof DeadBushBlock) {
                    player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    player.getHungerManager().add(1, -2.0F);
                    player.world.syncWorldEvent(6386, pos, 0);
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0F, 0.4F / (player.getRandom().nextFloat() * 0.2F + 0.7F));
                    player.swingHand(Hand.MAIN_HAND, true);
                }
            }
        });
    }
}
