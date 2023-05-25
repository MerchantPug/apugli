package net.merchantpug.apugli.mixin.xplatform.common;

import net.merchantpug.apugli.power.RedirectLightningPower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level {
    @Unique private boolean apugli$isLightningSkeletonHorse;

    protected ServerWorldMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void captureSkeletonHorseSpawn(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean bl, int i, int j, ProfilerFiller profiler, BlockPos blockPos, DifficultyInstance localDifficulty, boolean bl2) {
        apugli$isLightningSkeletonHorse = bl2;
    }

    @ModifyArg(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;atBottomCenterOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;"))
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
