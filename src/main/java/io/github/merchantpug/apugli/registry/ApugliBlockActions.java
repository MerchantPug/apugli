package io.github.merchantpug.apugli.registry;

import draylar.fabricfurnaces.block.FabricFurnaceBlock;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.apugli.Apugli;
import io.github.merchantpug.apugli.mixin.AbstractFurnaceBlockEntityAccessor;
import io.github.merchantpug.apugli.mixin.BrewingStandBlockEntityAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.apache.commons.lang3.tuple.Triple;

import static net.minecraft.state.property.Properties.LIT;

public class ApugliBlockActions {
    public static void register() {
        register(new ActionFactory<>(Apugli.identifier("light_up"), new SerializableData()
                .add("burn_time", SerializableDataTypes.INT)
                .add("brew_time", SerializableDataTypes.INT)
                .add("light_campfire", SerializableDataTypes.BOOLEAN, true)
                .add("particle", SerializableDataTypes.PARTICLE_TYPE, null)
                .add("particle_count", SerializableDataTypes.INT, 0)
                .add("sound", SerializableDataTypes.SOUND_EVENT, null),
                (data, block) -> {
                    BlockState state = block.getLeft().getBlockState(block.getMiddle());
                    BlockEntity entity = block.getLeft().getBlockEntity(block.getMiddle());
                    if (state.getBlock() instanceof AbstractFurnaceBlock) {
                        block.getLeft().setBlockState(block.getMiddle(), state.with(LIT, true).with(LIT, true), 2);
                        if (data.isPresent("particle") && data.getInt("particle_count") > 0 && !block.getLeft().isClient()) {
                            ((ServerWorld)block.getLeft()).spawnParticles((DefaultParticleType)data.get("particle"), block.getMiddle().getX() + 0.5, block.getMiddle().getY() + 0.3, block.getMiddle().getZ() + 0.5, data.getInt("particle_count"), block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.1D, block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.05D);
                        }
                        if (data.isPresent("sound")) {
                            block.getLeft().playSound(null, block.getMiddle().getX(), block.getMiddle().getY(), block.getMiddle().getZ(), (SoundEvent)data.get("sound"), SoundCategory.NEUTRAL, 0.5F, (block.getLeft().getRandom().nextFloat() - block.getLeft().getRandom().nextFloat()) * 0.2F + 1.0F);
                        }
                    }
                    if (entity instanceof AbstractFurnaceBlockEntity) {
                        if (((AbstractFurnaceBlockEntityAccessor)entity).getBurnTime() < data.getInt("burn_time")) {
                            ((AbstractFurnaceBlockEntityAccessor)entity).setFuelTime(data.getInt("burn_time"));
                            ((AbstractFurnaceBlockEntityAccessor)entity).setBurnTime(data.getInt("burn_time"));
                        }
                    }
                    if (state.getBlock() instanceof CampfireBlock && data.getBoolean("light_campfire")) {
                        block.getLeft().setBlockState(block.getMiddle(), state.with(LIT, true).with(LIT, true), 2);
                        if (data.isPresent("particle") && data.getInt("particle_count") > 0 && !block.getLeft().isClient()) {
                            ((ServerWorld)block.getLeft()).spawnParticles((DefaultParticleType)data.get("particle"), block.getMiddle().getX() + 0.5, block.getMiddle().getY() + 0.3, block.getMiddle().getZ() + 0.5, data.getInt("particle_count"), block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.1D, block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.05D);
                        }
                        if (data.isPresent("sound")) {
                            block.getLeft().playSound(null, block.getMiddle().getX(), block.getMiddle().getY(), block.getMiddle().getZ(), (SoundEvent)data.get("sound"), SoundCategory.NEUTRAL, 0.5F, (block.getLeft().getRandom().nextFloat() - block.getLeft().getRandom().nextFloat()) * 0.2F + 1.0F);
                        }
                    }
                    if (entity instanceof BrewingStandBlockEntity) {
                        if (((BrewingStandBlockEntityAccessor)entity).getFuel() < data.getInt("brew_time")) {
                            ((BrewingStandBlockEntityAccessor) entity).setFuel(data.getInt("brew_time"));
                        }
                        if (data.isPresent("particle") && data.getInt("particle_count") > 0 && !block.getLeft().isClient()) {
                            ((ServerWorld)block.getLeft()).spawnParticles((DefaultParticleType)data.get("particle"), block.getMiddle().getX() + 0.5, block.getMiddle().getY() + 0.3, block.getMiddle().getZ() + 0.5, data.getInt("particle_count"), block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.1D, block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.05D);
                        }
                        if (data.isPresent("sound")) {
                            block.getLeft().playSound(null, block.getMiddle().getX(), block.getMiddle().getY(), block.getMiddle().getZ(), (SoundEvent)data.get("sound"), SoundCategory.NEUTRAL, 0.5F, (block.getLeft().getRandom().nextFloat() - block.getLeft().getRandom().nextFloat()) * 0.2F + 1.0F);
                        }
                    }

                    if (FabricLoader.getInstance().isModLoaded("fabric-furnaces")) {
                        if (state.getBlock() instanceof FabricFurnaceBlock) {
                            block.getLeft().setBlockState(block.getMiddle(), state.with(LIT, true).with(LIT, true), 2);
                            if (data.isPresent("particle") && data.getInt("particle_count") > 0 && !block.getLeft().isClient()) {
                                ((ServerWorld)block.getLeft()).spawnParticles((DefaultParticleType)data.get("particle"), block.getMiddle().getX() + 0.5, block.getMiddle().getY() + 0.3, block.getMiddle().getZ() + 0.5, data.getInt("particle_count"), block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.1D, block.getLeft().getRandom().nextDouble() * 0.2D - 0.1D, 0.05D);
                            }
                            if (data.isPresent("sound")) {
                                block.getLeft().playSound(null, block.getMiddle().getX(), block.getMiddle().getY(), block.getMiddle().getZ(), (SoundEvent)data.get("sound"), SoundCategory.NEUTRAL, 0.5F, (block.getLeft().getRandom().nextFloat() - block.getLeft().getRandom().nextFloat()) * 0.2F + 1.0F);
                            }
                        }
                    }
                }));
        register(new ActionFactory<>(Apugli.identifier("destroy"), new SerializableData(),
                (data, block) -> {
                    CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(block.getLeft(), block.getMiddle(), true);
                    block.getLeft().syncWorldEvent(WorldEvents.BLOCK_BROKEN, block.getMiddle(), Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
                }));
    }

    private static void register(ActionFactory<Triple<World, BlockPos, Direction>> actionFactory) {
        Registry.register(ApoliRegistries.BLOCK_ACTION, actionFactory.getSerializerId(), actionFactory);
    }
}
