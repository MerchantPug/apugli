package io.github.merchantpug.apugli.action.block;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Random;

public class BonemealAction {
    public static void action(SerializableData.Instance data, Triple<World, BlockPos, Direction> block) {
        World world = block.getLeft();
        BlockPos blockPos = block.getMiddle();

        if (BoneMealItem.useOnFertilizable(ItemStack.EMPTY, world, blockPos) || BoneMealItem.useOnGround(ItemStack.EMPTY, world, blockPos.offset(block.getRight()), block.getRight())) {
            if (data.getBoolean("show_particles") && !world.isClient) {
                BonemealAction.createParticles(data, (ServerWorld)world, blockPos, 0);
            }
            if (!data.isPresent("sound")) return;
            world.playSound(null, blockPos, data.get("sound"), SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static void createParticles(SerializableData.Instance data, ServerWorld world, BlockPos pos, int count) {
        ParticleEffect effect = data.get("particle");

        if (count == 0) {
            count = 15;
        }

        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isAir()) {
            double d = 0.5D;
            double g;
            if (blockState.isOf(Blocks.WATER)) {
                count *= 3;
                g = 1.0D;
                d = 3.0D;
            } else if (blockState.isOpaqueFullCube(world, pos)) {
                pos = pos.up();
                count *= 3;
                d = 3.0D;
                g = 1.0D;
            } else {
                g = blockState.getOutlineShape(world, pos).getMax(Direction.Axis.Y);
            }

            world.spawnParticles(effect, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            Random random = world.getRandom();

            for(int i = 0; i < count; ++i) {
                double h = random.nextGaussian() * 0.02D;
                double j = random.nextGaussian() * 0.02D;
                double k = random.nextGaussian() * 0.02D;
                double l = 0.5D - d;
                double m = (double)pos.getX() + l + random.nextDouble() * d * 2.0D;
                double n = (double)pos.getY() + random.nextDouble() * g;
                double o = (double)pos.getZ() + l + random.nextDouble() * d * 2.0D;
                if (!world.getBlockState((new BlockPos(m, n, o)).down()).isAir()) {
                    world.spawnParticles(effect, m, n, o, 1, h, j, k,0.0D);
                }
            }

        }
    }

    public static ActionFactory<Triple<World, BlockPos, Direction>> getFactory() {
        return new ActionFactory<>(Apugli.identifier("bonemeal"),
                new SerializableData()
                        .add("show_particles", SerializableDataTypes.BOOLEAN, true)
                        .add("particle", SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, ParticleTypes.HAPPY_VILLAGER)
                        .add("sound", SerializableDataTypes.SOUND_EVENT, null),
                BonemealAction::action
        );
    }
}