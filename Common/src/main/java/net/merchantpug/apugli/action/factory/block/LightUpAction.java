package net.merchantpug.apugli.action.factory.block;

import net.merchantpug.apugli.action.factory.IActionFactory;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.AbstractFurnaceBlockEntityAccessor;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.merchantpug.apugli.mixin.xplatform.common.accessor.BrewingStandBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;

public class LightUpAction implements IActionFactory<Triple<Level, BlockPos, Direction>> {
    
    @Override
    public SerializableData getSerializableData() {
        return new SerializableData()
            .add("burn_time", SerializableDataTypes.INT, null)
            .add("brew_time", SerializableDataTypes.INT, null)
            .add("light_campfire", SerializableDataTypes.BOOLEAN, true)
            .add("particle", SerializableDataTypes.PARTICLE_TYPE, null)
            .add("particle_count", SerializableDataTypes.INT, 0)
            .add("sound", SerializableDataTypes.SOUND_EVENT, null);
    }
    
    @Override
    public void execute(SerializableData.Instance data, Triple<Level, BlockPos, Direction> block) {
        Level level = block.getLeft();
        BlockPos pos = block.getMiddle();
        BlockState state = level.getBlockState(pos);
        //Furnace-like
        if(
            data.isPresent("burn_time") &&
            level.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntityAccessor furnace
        ) {
            level.setBlock(pos, state.setValue(LIT, true), 2);
            int burnTime = data.getInt("burn_time");
            if(furnace.getCookingProgress() < burnTime) {
                furnace.setLitTime(burnTime);
                furnace.setCookingProgress(burnTime);
            }
        //Campfire
        } else if(
            data.getBoolean("light_campfire") &&
            state.getBlock() instanceof CampfireBlock &&
            state.getValue(LIT)
        ) {
            level.setBlock(block.getMiddle(), state.setValue(LIT, true), 2);
        //Brewing Stand
        } else if(
            data.isPresent("brew_time") &&
            level.getBlockEntity(pos) instanceof BrewingStandBlockEntityAccessor brewingStand
        ) {
            if(brewingStand.getFuel() < data.getInt("brew_time")) {
                brewingStand.setFuel(data.getInt("brew_time"));
            }
        } else return;
        //Play Sound
        if(data.isPresent("sound")) {
            level.playSound(null, pos, data.get("sound"), SoundSource.BLOCKS, 0.5F, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F + 1.0F);
        }
        //Particles
        if(data.isPresent("particle") && level instanceof ServerLevel serverLevel) {
            int count = data.getInt("particle_count");
            if(count > 0) {
                RandomSource random = level.random;
                serverLevel.sendParticles(data.get("particle"), pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, count, random.nextDouble() * 0.2 - 0.1, 0.1, random.nextDouble() * 0.2 - 0.1, 0.05);
            }
        }
    }

}
