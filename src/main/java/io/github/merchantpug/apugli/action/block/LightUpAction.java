package io.github.merchantpug.apugli.action.block;

import draylar.fabricfurnaces.block.FabricFurnaceBlock;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.mixin.AbstractFurnaceBlockEntityAccessor;
import io.github.merchantpug.apugli.mixin.BrewingStandBlockEntityAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import static net.minecraft.state.property.Properties.LIT;

public class LightUpAction {
    public static void action(SerializableData.Instance data, Triple<World, BlockPos, Direction> block) {
        lightFurnace(data, block);
        lightCampfire(data, block);
        lightBrewingStand(data, block);
    }

    private static void lightFurnace(SerializableData.Instance data, Triple<World, BlockPos, Direction> block) {
        BlockState state = block.getLeft().getBlockState(block.getMiddle());
        BlockEntity entity = block.getLeft().getBlockEntity(block.getMiddle());
        if (!(state.getBlock() instanceof AbstractFurnaceBlock) && FabricLoader.getInstance().isModLoaded("fabric-furnaces") && !(state.getBlock() instanceof FabricFurnaceBlock)) return;
        if (!(entity instanceof AbstractFurnaceBlockEntity)) return;
        block.getLeft().setBlockState(block.getMiddle(), state.with(LIT, true).with(LIT, true), 2);
        spawnParticles(block, (ParticleType<?>)data.get("particle"), data.getInt("particle_count"));
        playSound(block, (SoundEvent)data.get("sound"));
        if (((AbstractFurnaceBlockEntityAccessor)entity).getBurnTime() < data.getInt("burn_time")) {
            ((AbstractFurnaceBlockEntityAccessor)entity).setFuelTime(data.getInt("burn_time"));
            ((AbstractFurnaceBlockEntityAccessor)entity).setBurnTime(data.getInt("burn_time"));
        }
    }

    private static void lightCampfire(SerializableData.Instance data, Triple<World, BlockPos, Direction> block) {
        BlockState state = block.getLeft().getBlockState(block.getMiddle());
        if (!(state.getBlock() instanceof CampfireBlock) || !data.getBoolean("light_campfire")) return;
        block.getLeft().setBlockState(block.getMiddle(), state.with(LIT, true).with(LIT, true), 2);
        spawnParticles(block, (ParticleType<?>)data.get("particle"), data.getInt("particle_count"));
        playSound(block, (SoundEvent)data.get("sound"));
    }

    private static void lightBrewingStand(SerializableData.Instance data, Triple<World, BlockPos, Direction> block) {
        BlockEntity entity = block.getLeft().getBlockEntity(block.getMiddle());
        if (!(entity instanceof BrewingStandBlockEntity)) return;
        if (((BrewingStandBlockEntityAccessor) entity).getFuel() < data.getInt("brew_time")) ((BrewingStandBlockEntityAccessor) entity).setFuel(data.getInt("brew_time"));
        spawnParticles(block, (ParticleType<?>)data.get("particle"), data.getInt("particle_count"));
        playSound(block, (SoundEvent)data.get("sound"));
    }

    private static void spawnParticles(Triple<World, BlockPos, Direction> block, ParticleType<?> particle, int particleCount) {
        if (particle != null && particleCount > 0 && !block.getLeft().isClient()) ((ServerWorld)block.getLeft()).spawnParticles((DefaultParticleType)particle, block.getMiddle().getX() + 0.5, block.getMiddle().getY() + 0.3, block.getMiddle().getZ() + 0.5, particleCount, block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.1D, block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.05D);
    }

    private static void playSound(Triple<World, BlockPos, Direction> block, SoundEvent sound) {
        if (sound == null) return;
        block.getLeft().playSound(null, block.getMiddle().getX(), block.getMiddle().getY(), block.getMiddle().getZ(), sound, SoundCategory.NEUTRAL, 0.5F, (block.getLeft().getRandom().nextFloat() - block.getLeft().getRandom().nextFloat()) * 0.2F + 1.0F);
    }

    public static ActionFactory<Triple<World, BlockPos, Direction>> getFactory() {
        return new ActionFactory<>(Apugli.identifier("light_up"), new SerializableData()
                .add("burn_time", SerializableDataTypes.INT)
                .add("brew_time", SerializableDataTypes.INT)
                .add("light_campfire", SerializableDataTypes.BOOLEAN, true)
                .add("particle", SerializableDataTypes.PARTICLE_TYPE, null)
                .add("particle_count", SerializableDataTypes.INT, 0)
                .add("sound", SerializableDataTypes.SOUND_EVENT, null),
                LightUpAction::action
        );
    }
}
