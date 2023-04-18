<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/BeehiveBlockMixin.java
package net.merchantpug.apugli.mixin;

import net.merchantpug.apugli.power.PreventBeeAngerPower;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
========
package com.github.merchantpug.apugli.mixin.xplatforn.common;

import com.github.merchantpug.apugli.mixin.xplatforn.common.accessor.BeehiveBlockEntityAccessor;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/BeehiveBlockMixin.java
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import the.great.migration.merchantpug.apugli.power.PreventBeeAngerPower;

import java.util.List;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {
    @Inject(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BeehiveBlockEntity;angerBees(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BeehiveBlockEntity$BeeState;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void releaseBeesIfAngerPrevented(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci, BeehiveBlockEntity beehiveBlockEntity) {
        if(!PowerHolderComponent.hasPower(player, PreventBeeAngerPower.class)) return;
        ((BeehiveBlockEntityAccessor)beehiveBlockEntity).invokeTryReleaseBee(state, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
    }

<<<<<<<< HEAD:src/main/java/net/merchantpug/apugli/mixin/BeehiveBlockMixin.java
    @Inject(method = "angerNearbyBees", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void dontAngerBees(World world, BlockPos pos, CallbackInfo ci, List<BeeEntity> list, List<PlayerEntity> list2) {
        if (list2.stream().anyMatch(player -> PowerHolderComponent.hasPower(player, PreventBeeAngerPower.class))) {
            ci.cancel();
========
    @ModifyArg(method = "angerNearbyBees", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BeeEntity;setTarget(Lnet/minecraft/entity/LivingEntity;)V"))
    private LivingEntity dontAngerBees(LivingEntity entity) {
        if(PowerHolderComponent.hasPower(entity, PreventBeeAngerPower.class)) {
            return entity = null;
>>>>>>>> pr/25:Common/src/main/java/com/github/merchantpug/apugli/mixin/xplatforn/common/BeehiveBlockMixin.java
        }
    }
}
