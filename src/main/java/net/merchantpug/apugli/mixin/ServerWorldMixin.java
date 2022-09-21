package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.power.RedirectLightningPower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @Unique private boolean apugli$isLightningSkeletonHorse;

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;create(Lnet/minecraft/world/World;)Lnet/minecraft/entity/Entity;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureSkeletonHorseSpawn(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, Profiler profiler, BlockPos blockPos, LocalDifficulty localDifficulty, boolean bl2) {
        apugli$isLightningSkeletonHorse = bl2;
    }

    @ModifyArg(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;ofBottomCenter(Lnet/minecraft/util/math/Vec3i;)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3i redirectLightningToPowerHolder(Vec3i vec) {
        if (!apugli$isLightningSkeletonHorse) {
            LivingEntity target = null;
            List<Map.Entry<LivingEntity, Float>> list = RedirectLightningPower.STRUCK_BY_LIGHTNING_CHANCES.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();
            for (Map.Entry<LivingEntity, Float> map : list) {
                if (!this.hasRain(map.getKey().getBlockPos())) continue;
                if (this.random.nextDouble() < map.getValue()) {
                    target = map.getKey();
                    break;
                }
            }
            if (target != null) {
                return target.getBlockPos();
            }
        }
        return vec;
    }
}
