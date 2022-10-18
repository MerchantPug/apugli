package com.github.merchantpug.apugli.mixin.xplatforn.common;

import the.great.migration.merchantpug.apugli.power.RedirectLightningPower;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level {
    @Unique private boolean apugli$isLightningSkeletonHorse;

    protected ServerWorldMixin(WritableLevelData properties, ResourceKey<Level> registryRef, Holder<DimensionType> dimension, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureSkeletonHorseSpawn(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, ProfilerFiller profiler, BlockPos blockPos, DifficultyInstance localDifficulty, boolean bl2) {
        apugli$isLightningSkeletonHorse = bl2;
    }

    @ModifyArg(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;ofBottomCenter(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3i redirectLightningToPowerHolder(Vec3i vec) {
        if(!apugli$isLightningSkeletonHorse) {
            LivingEntity target = null;
            List<Map.Entry<LivingEntity, Float>> list = RedirectLightningPower.STRUCK_BY_LIGHTNING_CHANCES.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();
            for(Map.Entry<LivingEntity, Float> map : list) {
                if(!this.isRainingAt(map.getKey().blockPosition())) continue;
                if(this.random.nextDouble() < map.getValue()) {
                    target = map.getKey();
                    break;
                }
            }
            if(target != null) {
                return target.blockPosition();
            }
        }
        return vec;
    }
}
