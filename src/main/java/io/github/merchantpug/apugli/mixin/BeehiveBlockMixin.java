package io.github.merchantpug.apugli.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.merchantpug.apugli.power.PreventBeeAngerPower;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin {
    @Inject(method = "afterBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BeehiveBlockEntity;angerBees(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/entity/BeehiveBlockEntity$BeeState;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void releaseBeesIfAngerPrevented(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci, BeehiveBlockEntity beehiveBlockEntity) {
        if (!PowerHolderComponent.hasPower(player, PreventBeeAngerPower.class)) return;
        ((BeehiveBlockEntityAccessor)beehiveBlockEntity).invokeTryReleaseBee(state, BeehiveBlockEntity.BeeState.EMERGENCY);
    }

    @ModifyArg(method = "angerNearbyBees", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BeeEntity;setTarget(Lnet/minecraft/entity/LivingEntity;)V"))
    private LivingEntity dontAngerBees(LivingEntity entity) {
        if (PowerHolderComponent.hasPower(entity, PreventBeeAngerPower.class)) {
            return entity = null;
        }
        return entity;
    }
}
